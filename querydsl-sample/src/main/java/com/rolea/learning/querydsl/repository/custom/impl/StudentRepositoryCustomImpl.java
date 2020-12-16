package com.rolea.learning.querydsl.repository.custom.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.rolea.learning.querydsl.domain.QAddress;
import com.rolea.learning.querydsl.domain.QCourse;
import com.rolea.learning.querydsl.domain.QGrade;
import com.rolea.learning.querydsl.domain.QStudent;
import com.rolea.learning.querydsl.domain.Student;
import com.rolea.learning.querydsl.repository.custom.StudentRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * Joins are required in order to force a JOIN fetch
 * - inner joins for required dependencies
 * - left joins for optional dependencies
 */
@Component
public class StudentRepositoryCustomImpl implements StudentRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    private final QStudent qStudent = QStudent.student;
    private final QAddress qAddress = QAddress.address;
    private final QGrade qGrade = QGrade.grade;
    private final QCourse qCourse = QCourse.course;

    @Override
    public Optional<Student> findByFirstName(String firstName) {
        return ofNullable(new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .where(qStudent.firstName.eq(firstName))
                .fetchOne());
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .where(qStudent.lastName.eq(lastName))
                .fetch();
    }

    @Override
    public List<Student> findByCity(String city) {
        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .where(qStudent.address.city.eq(city))
                .fetch();
    }

    @Override
    public List<Student> findByGrade(Double grade) {
        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .leftJoin(qStudent.grades, qGrade).fetchJoin()
                .where(qGrade.gradeValue.eq(grade))
                .distinct()
                .fetch();
    }

    @Override
    public List<Student> findByCourse(String course) {
        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .leftJoin(qStudent.grades, qGrade).fetchJoin()
                .leftJoin(qStudent.courses, qCourse).fetchJoin()
                .where(qCourse.courseName.eq("First"))
                .distinct()
                .fetch();
    }

    @Override
    public List<Student> findByAllCourses(List<String> courses) {
        Predicate predicate = ExpressionUtils.allOf(courses.stream()
                .map(course -> qStudent.courses.any().courseName.eq(course))
                .collect(toList()));

        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .leftJoin(qStudent.grades, qGrade).fetchJoin()
                .leftJoin(qStudent.courses, qCourse).fetchJoin()
                .where(predicate)
                .distinct()
                .fetch();
    }

    @Override
    public List<Student> findByAnyCourses(List<String> courses) {
        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .leftJoin(qStudent.grades, qGrade).fetchJoin()
                .leftJoin(qStudent.courses, qCourse).fetchJoin()
                .where(qCourse.courseName.in(courses))
                .distinct()
                .fetch();
    }

    @Override
    public List<Student> findAll(int pageIndex, int pageSize) {
        return new JPAQuery<Student>(entityManager)
                .from(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .offset(pageIndex * pageSize)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public Long countStudents() {
        return new JPAQuery<Long>(entityManager)
                .select(qStudent.countDistinct())
                .from(qStudent)
                .fetchCount();
    }

}
