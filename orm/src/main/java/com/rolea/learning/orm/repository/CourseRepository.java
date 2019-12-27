package com.rolea.learning.orm.repository;

import com.rolea.learning.orm.domain.Course;
import com.rolea.learning.orm.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByStudentsIn(List<Student> students);

}
