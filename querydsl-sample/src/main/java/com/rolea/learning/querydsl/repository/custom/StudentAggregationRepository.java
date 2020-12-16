package com.rolea.learning.querydsl.repository.custom;

import com.rolea.learning.querydsl.domain.Student;

import java.util.List;
import java.util.Map;

public interface StudentAggregationRepository {

    Map<Double, List<Student>> groupStudentsByGrades();

    Map<Long, Double> computeGradeAveragePerStudent();

    Map<String, List<Student>> groupStudentsByCourses();

    Map<String, Long> computeStudentCountPerCourse();

}
