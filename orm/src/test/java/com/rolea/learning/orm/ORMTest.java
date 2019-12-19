package com.rolea.learning.orm;

import com.rolea.learning.orm.domain.Student;
import com.rolea.learning.orm.repository.StudentRepository;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ORMTest {

	@Autowired
	private StudentRepository repository;

	@Test
	void contextLoads(){
		assertThat(repository)
				.as("StudentRepository is successfully initialized")
				.isNotNull();
	}

	@Test
	@Ignore
	void test(){
		// TODO review https://github.com/hibernate/hibernate-orm/blob/master/etc/hibernate.properties
		System.out.println(repository.save(Student.builder().firstName("John").lastName("Doe").build()));
		System.out.println(repository.findById(1L));
	}

}
