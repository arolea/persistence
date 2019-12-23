package com.rolea.learning.orm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hibernate reference - https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/index.html
 * Hibernate user guide - https://docs.jboss.org/hibernate/stable/orm/userguide/html_single/Hibernate_User_Guide.html
 */
@SpringBootApplication
public class ORMApplication implements CommandLineRunner {

//	@Autowired
//	private StudentRepository studentRepository;

	public static void main(String[] args) {
		SpringApplication.run(ORMApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		Address address = new Address();
//		address.setStreet("Street");
//		address.setCity("City");
//
//		Student student = new Student();
//		student.setFirstName("John");
//		student.setLastName("Doe");
//		student.setAddress(address);
//
//		student = studentRepository.save(student);
	}
}
