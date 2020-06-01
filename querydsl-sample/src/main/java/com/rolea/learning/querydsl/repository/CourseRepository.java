package com.rolea.learning.querydsl.repository;

import com.rolea.learning.querydsl.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
