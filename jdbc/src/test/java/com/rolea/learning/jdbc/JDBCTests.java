package com.rolea.learning.jdbc;

import com.rolea.learning.jdbc.domain.Student;
import com.rolea.learning.jdbc.service.StudentDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JDBCTests {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private StudentDAO studentDAO;

	@Test
	void contextLoads(){
		assertThat(dataSource)
				.as("DataSource is properly initialized")
				.isNotNull();

		assertThat(jdbcTemplate)
				.as("JdbcTemplate is properly initialized")
				.isNotNull();

		assertThat(studentDAO)
				.as("StudentDAO is properly initialized")
				.isNotNull();
	}

	@Test
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	void testDao(){
		Student student = Student.builder()
				.firstName("John")
				.lastName("Doe")
				.build();

		long studentId = studentDAO.save(student);
		assertThat(studentId)
				.as("Student id is generated upon insert")
				.isNotEqualTo(0L);

		long studentCount = studentDAO.count();
		assertThat(studentCount)
				.as("Student count is 1 after a successful insert")
				.isEqualTo(1);

		Optional<Student> dbStudentOptional = studentDAO.findOne(studentId);
		assertThat(dbStudentOptional.isPresent())
				.as("A student is returned from db upon fetch by id")
				.isTrue();
		Student dbStudent = dbStudentOptional.get();
		assertThat(dbStudent.getFirstName())
				.as("First name is properly fetched from db")
				.isEqualTo("John");
		assertThat(dbStudent.getLastName())
				.as("Last name is properly fetched from the db")
				.isEqualTo("Doe");

		studentDAO.delete(studentId);
		studentCount = studentDAO.count();
		assertThat(studentCount)
				.as("Student count is 0 after a successful delete")
				.isEqualTo(0);

		List<Student> students = asList(
				Student.builder()
						.firstName("Jane")
						.lastName("Doe")
						.build(),
				Student.builder()
						.firstName("Jake")
						.lastName("Doe")
						.build()
		);
		studentDAO.saveAll(students);

		students = studentDAO.findAll();
		assertThat(students.size())
				.as("There are three students fetched by find all")
				.isEqualTo(2);
		students.forEach(currentStudent -> {
			assertThat(currentStudent.getFirstName())
					.as("The first name is either Jane or Jake")
					.isIn("Jane", "Jake");
			assertThat(currentStudent.getLastName())
					.as("The last name is Doe")
					.isEqualTo("Doe");
		});

	}

	@Test
	void testBatchPerformance(){
		int numberOfInserts = 1000;

		List<Student> students = IntStream.range(0, numberOfInserts)
				.mapToObj(index -> Student.builder()
						.firstName(format("John %d", index))
						.lastName("Doe")
						.build()
				)
				.collect(toList());

		long beginWithoutBatch = System.currentTimeMillis();
		students.forEach(student -> studentDAO.save(student));
		long endWithoutBatch = System.currentTimeMillis();
		long timeWithoutBatch = endWithoutBatch - beginWithoutBatch;

		long beginWithBatch = System.currentTimeMillis();
		studentDAO.saveAll(students);
		long endWithBatch = System.currentTimeMillis();
		long timeWithBatch = endWithBatch - beginWithBatch;

		assertThat(timeWithoutBatch)
				.as("Inserting without batching takes more than inserting with batching")
				.isGreaterThan(timeWithBatch);
	}



}
