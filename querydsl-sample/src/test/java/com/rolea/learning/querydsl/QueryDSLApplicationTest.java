package com.rolea.learning.querydsl;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQuery;
import com.rolea.learning.querydsl.domain.QAddress;
import com.rolea.learning.querydsl.domain.QCourse;
import com.rolea.learning.querydsl.domain.QGrade;
import com.rolea.learning.querydsl.domain.QStudent;
import com.rolea.learning.querydsl.domain.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest(properties = {""})
@TestPropertySource(value = "classpath:persistence-test.properties")
@Sql(scripts = "classpath:test-data.sql")
@Slf4j
public class QueryDSLApplicationTest {

    @Autowired
    private EntityManager entityManager;

    private QStudent qStudent = QStudent.student;
    private QAddress qAddress = QAddress.address;
    private QGrade qGrade = QGrade.grade;
    private QCourse qCourse = QCourse.course;
    private JPAQuery<Student> query;

    /**
     * Groups students by grades via an application side group by
     */
    @Test
    public void testGroupByApplicationSide() {
        query = new JPAQuery<>(entityManager);
        Map<Double, List<Student>> studentMap = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .transform(GroupBy.groupBy(qStudent.grades.any().gradeValue).as(GroupBy.list(qStudent)));

        assertThat(studentMap.size()).isEqualTo(2);
        assertThat(studentMap.get(10D).size()).isEqualTo(2);
        assertThat(studentMap.get(5D).size()).isEqualTo(1);
    }

    /**
     * Computes the average grade for students on the DB side
     */
    @Test
    public void testGroupByDBSide_gradeAverage() {
        query = new JPAQuery<>(entityManager);
        Map<Long, Double> gradeAverage = query
                .from(qStudent)
                .groupBy(qStudent.studentId)
                .transform(GroupBy.groupBy(qStudent.studentId).as(qStudent.grades.any().gradeValue.avg()));

        assertThat(gradeAverage.values().containsAll(List.of(7.5D, 10D))).isTrue();
    }

    /**
     * Computes the student list and student count per course on the DB side
     */
    @Test
    public void testGroupByDBSide_studentsForCourse() {
        log.info("Aggregating to Student");
        query = new JPAQuery<>(entityManager);
        Map<String, List<Student>> courseMap = query
                .from(qCourse)
                .innerJoin(qCourse.students, qStudent)
                .innerJoin(qStudent.address, qAddress).fetchJoin()
                .groupBy(qCourse.courseName, qStudent.studentId)
                .transform(GroupBy.groupBy(qCourse.courseName).as(GroupBy.list(qStudent)));

        assertThat(courseMap.get("First").size()).isEqualTo(2);
        assertThat(courseMap.get("Second").size()).isEqualTo(1);
        assertThat(courseMap.get("Third").size()).isEqualTo(1);

        log.info("Aggregating to Student count");
        query = new JPAQuery<>(entityManager);
        Map<String, Long> courseStudentCount = query
                .from(qCourse)
                .innerJoin(qCourse.students, qStudent)
                .groupBy(qCourse.courseName)
                .transform(GroupBy.groupBy(qCourse.courseName).as(qStudent.studentId.count()));

        assertThat(courseStudentCount.get("First")).isEqualTo(2L);
        assertThat(courseStudentCount.get("Second")).isEqualTo(1L);
        assertThat(courseStudentCount.get("Third")).isEqualTo(1L);
    }

}
