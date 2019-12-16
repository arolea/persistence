package com.rolea.learning.jdbc.service;

import com.rolea.learning.jdbc.domain.Student;

import java.util.List;
import java.util.Optional;

public interface StudentDAO {

	Optional<Student> findOne(Long id);

	List<Student> findAll();

	Long save(Student student);

	void saveAll(List<Student> students);

	void delete(Long id);

	Long count();

}
