package com.rolea.learning.orm.repository;

import com.rolea.learning.orm.domain.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

	@Query("SELECT s FROM Student s")
	Slice<Student> findAllStudents(Pageable pageable);

}
