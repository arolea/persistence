package com.rolea.learning.querydsl.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rolea.learning.querydsl.domain.*;
import com.rolea.learning.querydsl.repository.custom.StudentRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
@Slf4j
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

        // for more complex expressions BooleanBuilder can be used instead
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        courses.forEach(course -> booleanBuilder.and(qStudent.courses.any().courseName.eq(course)));

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

    @Override
    public List<Student> findByCourseSubQuery(String course) {
        // example of using the QueryFactory
        Query query = new JPAQueryFactory(entityManager).selectFrom(qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .leftJoin(qStudent.grades, qGrade).fetchJoin()
                .leftJoin(qStudent.courses, qCourse).fetchJoin()
                // example of using subquery
                .where(qCourse.courseId.in(JPAExpressions
                        .select(qCourse.courseId)
                        .from(qCourse)
                        .where(qCourse.courseName.eq(course))))
                .distinct()
                .createQuery();

        // you can tune the query here

        return query.getResultList();
    }

    @Override
    public List<Long> findStudentIdsByCourse(String course) {
        // sample Tuple usage for projections
        List<Tuple> result = new JPAQuery<Tuple>(entityManager)
                .select(qStudent.studentId, qCourse.courseId)
                .from(qStudent, qCourse)
                .where(qCourse.courseName.eq("First"))
                .distinct()
                .fetch();

        return result.stream()
                .peek(tuple -> log.info("Course id: {}", tuple.get(qCourse.courseId)))
                .map(tuple -> tuple.get(qStudent.studentId))
                .collect(toList());
    }

    @Override
    public List<Student> findStudentProjectionByCourse(String course) {
        return new JPAQuery<Student>(entityManager)
                .select(Projections.bean(Student.class, qStudent.studentId, qStudent.firstName))
                .from(qStudent)
                .where(qStudent.courses.any().courseName.eq("First"))
                .distinct()
                .fetch();
    }

}
