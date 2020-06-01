package com.rolea.learning.querydsl.repository;

import com.rolea.learning.querydsl.domain.Grade;
import com.rolea.learning.querydsl.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findAllByStudent(Student student);

}
