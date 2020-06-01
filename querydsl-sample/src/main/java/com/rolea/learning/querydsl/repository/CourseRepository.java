package com.rolea.learning.querydsl.repository;

import com.rolea.learning.querydsl.domain.Course;
import com.rolea.learning.querydsl.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByStudentsIn(List<Student> students);

}
