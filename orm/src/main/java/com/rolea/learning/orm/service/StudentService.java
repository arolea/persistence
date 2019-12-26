package com.rolea.learning.orm.service;

import com.rolea.learning.orm.domain.Student;
import com.rolea.learning.orm.repository.StudentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
public class StudentService {

	@Autowired
	private StudentRepository repository;

	@Transactional
	public Student loadStudentWithGrades(Long studentId) {
		return repository.findById(studentId)
				.map(student -> {
					log.info("Triggering update on entity");
					student.setFirstName("Jane");
					log.info("Triggering lazy load for grades");
					student.getGrades().size();
					log.info("Triggering lazy load for courses");
					student.getCourses().size();
					return student;
				})
				.orElseThrow(() -> new RuntimeException("Student not found"));
	}

}
