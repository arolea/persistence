package com.rolea.learning.querydsl.repository.custom.impl;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQuery;
import com.rolea.learning.querydsl.domain.QAddress;
import com.rolea.learning.querydsl.domain.QCourse;
import com.rolea.learning.querydsl.domain.QGrade;
import com.rolea.learning.querydsl.domain.QStudent;
import com.rolea.learning.querydsl.domain.Student;
import com.rolea.learning.querydsl.repository.custom.StudentAggregationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@Component
public class StudentAggregationRepositoryImpl implements StudentAggregationRepository {

    @Autowired
    private EntityManager entityManager;

    private final QStudent qStudent = QStudent.student;
    private final QAddress qAddress = QAddress.address;
    private final QGrade qGrade = QGrade.grade;
    private final QCourse qCourse = QCourse.course;

    // application side
    @Override
    public Map<Double, List<Student>> groupStudentsByGrades() {
        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .transform(GroupBy.groupBy(qStudent.grades.any().gradeValue).as(GroupBy.list(qStudent)));
    }

    // db side
    @Override
    public Map<Long, Double> computeGradeAveragePerStudent() {
        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .groupBy(qStudent.studentId)
                .transform(GroupBy.groupBy(qStudent.studentId).as(qStudent.grades.any().gradeValue.avg()));
    }

    // db side
    @Override
    public Map<String, List<Student>> groupStudentsByCourses() {
        return new JPAQuery<Student>(entityManager)
                .from(qCourse)
                .innerJoin(qCourse.students, qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .groupBy(qCourse.courseName, qStudent.studentId)
                .transform(GroupBy.groupBy(qCourse.courseName).as(GroupBy.list(qStudent)));
    }

    // db side
    @Override
    public Map<String, Long> computeStudentCountPerCourse() {
        return new JPAQuery<Student>(entityManager)
                .from(qCourse)
                .innerJoin(qCourse.students, qStudent)
                .groupBy(qCourse.courseName)
                .transform(GroupBy.groupBy(qCourse.courseName).as(qStudent.studentId.count()));
    }

}
