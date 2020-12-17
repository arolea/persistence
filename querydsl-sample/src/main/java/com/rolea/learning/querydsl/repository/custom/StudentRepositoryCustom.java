package com.rolea.learning.querydsl.repository.custom;

import com.rolea.learning.querydsl.domain.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepositoryCustom {

    Optional<Student> findByFirstName(String firstName);

    List<Student> findByLastName(String lastName);

    List<Student> findByCity(String city);

    List<Student> findByGrade(Double grade);

    List<Student> findByCourse(String course);

    List<Student> findByAllCourses(List<String> courses);

    List<Student> findByAnyCourses(List<String> courses);

    List<Student> findAll(int pageIndex, int pageSize);

    Long countStudents();

    List<Student> findByCourseSubQuery(String course);

    List<Long> findStudentIdsByCourse(String course);

    List<Student> findStudentProjectionByCourse(String course);

}
