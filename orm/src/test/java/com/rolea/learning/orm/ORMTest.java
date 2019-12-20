package com.rolea.learning.orm;

import com.rolea.learning.orm.domain.Address;
import com.rolea.learning.orm.domain.Grade;
import com.rolea.learning.orm.domain.Student;
import com.rolea.learning.orm.repository.AddressRepository;
import com.rolea.learning.orm.repository.GradeRepository;
import com.rolea.learning.orm.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ORMTest {

	@Autowired
	private StudentRepository studentRepository;

	// Normally, you should not need a repository for the managed side of a one-to-one relationship
	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private GradeRepository gradeRepository;

	// TODO review https://github.com/hibernate/hibernate-orm/blob/master/etc/hibernate.properties

	/**
	 * You can persist the managed side of a one to one relationship via the owning side
	 */
	@Test
	void manage_one_to_one_via_owning_side(){
		Student student = studentRepository.save(Student.builder()
				.firstName("John")
				.lastName("Doe")
				.address(Address.builder()
						.street("Street")
						.city("City")
						.build())
				.build());
		Optional<Student> studentOptional = studentRepository.findById(student.getStudentId());
		Optional<Address> addressOptional = addressRepository.findById(student.getAddress().getAddressId());

		assertThat(studentOptional.isPresent())
				.as("Student is properly fetched from the DB via its onw repository")
				.isTrue();
		studentOptional.ifPresent(stud -> assertThat(stud.getAddress())
				.as("Address is eagerly fetched")
				.isNotNull());
		assertThat(addressOptional.isPresent())
				.as("Address is properly fetched from the DB via its own repository")
				.isTrue();
	}

	/**
	 * You can't persist the owning side of a one to one relationship via the managed side
	 */
	@Test
	void manage_one_to_one_via_managed_side(){
		assertThrows(InvalidDataAccessApiUsageException.class, () -> addressRepository.save(Address.builder()
				.street("Street")
				.city("City")
				.student(Student.builder()
						.firstName("John")
						.lastName("Doe")
						.build())
				.build()));
	}

	@Test
	void manage_one_to_many_via_owning_side(){
		Student student = studentRepository.save(Student.builder()
				.firstName("John")
				.lastName("Doe")
				.grades(asList(
						Grade.builder()
								.grade(7.5)
								.build(),
						Grade.builder()
								.grade(8.3)
								.build()
				))
				.build());

		System.out.println(student);

	}

}
