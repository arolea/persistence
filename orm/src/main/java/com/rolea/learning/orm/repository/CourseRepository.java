package com.rolea.learning.orm.repository;

import com.rolea.learning.orm.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {



}
