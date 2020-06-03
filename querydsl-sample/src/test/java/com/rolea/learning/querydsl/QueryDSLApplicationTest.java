package com.rolea.learning.querydsl;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.rolea.learning.querydsl.domain.*;
import com.rolea.learning.querydsl.repository.AddressRepository;
import com.rolea.learning.querydsl.repository.CourseRepository;
import com.rolea.learning.querydsl.repository.GradeRepository;
import com.rolea.learning.querydsl.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class QueryDSLApplicationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private DataUtils dataUtils;

    @Autowired
    private EntityManager entityManager;

    private QStudent qStudent = QStudent.student;
    private QAddress qAddress = QAddress.address;
    private QGrade qGrade = QGrade.grade;
    private QCourse qCourse = QCourse.course;
    private JPAQuery<Student> query;

    @BeforeEach
    public void setUp() {
        log.info("DB Setup begin");
        dataUtils.populateData();
        log.info("DB Setup end");
    }

    @AfterEach
    public void tearDown() {
        log.info("DB Clean-up begin");
        dataUtils.cleanData();
        log.info("DB Clean-up end");
    }

    @Test
    public void testContextInit() {
        assertThat(studentRepository.count()).isEqualTo(2L);
        assertThat(addressRepository.count()).isEqualTo(2L);
        assertThat(gradeRepository.count()).isEqualTo(3L);
        assertThat(courseRepository.count()).isEqualTo(3L);
    }

    /**
     * Examples of fetching students by their names (first name and last name)
     */
    @Test
    public void testFetchByStudentName() {
        log.info("Testing fetch that has a single result");
        query = new JPAQuery<>(entityManager);
        Student student = query.from(qStudent)
                // required to fetch the address in the same query as the student
                .innerJoin(qStudent.address).fetchJoin()
                .where(qStudent.firstName.eq("John"))
                .fetchOne();

        // test student data is properly fetched
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Doe");
        // test address data in properly fetched
        assertThat(student.getAddress().getAddressId()).isNotNull();

        log.info("Testing fetch that has multiple results");
        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                // required to fetch the address in the same query as the student
                .innerJoin(qStudent.address).fetchJoin()
                .where(qStudent.lastName.eq("Doe"))
                .fetch();

        // test all relevant students are fetched
        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            // test all relevant data is properly fetched for each student
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
        });
    }

    /**
     * Examples of fetching students by their city (student -> address -> city)
     */
    @Test
    public void testFetchByAddressCity() {
        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .where(qStudent.address.city.eq("City"))
                .fetch();

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
        });
    }

    /**
     * Example of fetching students associated with a given grade
     */
    @Test
    public void testFetchByGradeValue() {
        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                // required to fetch the address in the same query as the student
                .innerJoin(qStudent.address).fetchJoin()
                // required to fetch the grades in the same query as the students, without this grades are lazily loaded
                .innerJoin(qStudent.grades).fetchJoin()
                .where(qStudent.grades.any().gradeValue.eq(10D))
                .distinct()
                .fetch();

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
            currentStudent.getGrades().forEach(currentGrade -> assertThat(currentGrade.getGradeId()).isNotNull());
        });
    }

    /**
     * Example of fetching students associated with a given course
     */
    @Test
    public void testFetchByCourseName() {
        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                // required to fetch the address in the same query as the student
                .innerJoin(qStudent.address).fetchJoin()
                // required to fetch the grades in the same query as the students, without this grades are lazily loaded
                .innerJoin(qStudent.grades).fetchJoin()
                // required to fetch the courses in the same query as the student, without this courses are lazily loaded
                .innerJoin(qStudent.courses).fetchJoin()
                .where(qStudent.courses.any().courseName.eq("First"))
                .distinct()
                .fetch();

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
            currentStudent.getGrades().forEach(currentGrade -> assertThat(currentGrade.getGradeId()).isNotNull());
            currentStudent.getCourses().forEach(currentCourse -> assertThat(currentCourse.getCourseId()).isNotNull());
        });
    }

    /**
     * Example of fetching students associated with a list of photo ids
     */
    @Test
    public void testFetchByPhotoIds(){
        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                // required to fetch the address in the same query as the student
                .innerJoin(qStudent.address).fetchJoin()
                .where(qStudent.studentPhotoIds.any().in(1))
                .distinct()
                .fetch();

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
        });
    }

    /**
     * Fetch all Students associated with ALL of the given courses
     */
    @Test
    public void testFetchMatchingAllCourses() {
        List<String> courseList = List.of("First", "Third");
        // add any additional filtering logic here
        Predicate predicate = ExpressionUtils.allOf(courseList.stream()
                .map(course -> qStudent.courses.any().courseName.eq(course))
                .collect(toList()));

        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .innerJoin(qStudent.grades).fetchJoin()
                .innerJoin(qStudent.courses).fetchJoin()
                .where(predicate)
                .distinct()
                .fetch();

        assertThat(studentList.size()).isEqualTo(1);
    }

    /**
     * Fetch all Students associated with ANY of the given courses
     */
    @Test
    public void testFetchMatchingAnyCourses() {
        List<String> classesList = List.of("First", "Second");
        Predicate predicate = ExpressionUtils.anyOf(classesList.stream()
                .map(course -> qStudent.courses.any().courseName.eq(course))
                .collect(toList()));

        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .innerJoin(qStudent.grades).fetchJoin()
                .innerJoin(qStudent.courses).fetchJoin()
                .where(predicate)
                .distinct()
                .fetch();

        assertThat(studentList.size()).isEqualTo(2);
    }

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

    /**
     * Fetches the max grade from the DB
     */
    @Test
    public void testGetMaxGrade() {
        query = new JPAQuery<>(entityManager);
        Double maxGrade = query
                .from(qGrade)
                .select(qGrade.gradeValue.max())
                .fetchFirst();

        assertThat(maxGrade).isEqualTo(10D);
    }

    /**
     * Pagination example for students
     */
    @Test
    public void testPagination() {
        int pageSize = 1;
        int pageIndex = 0;

        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .offset(pageIndex * pageSize)
                .limit(pageSize)
                .fetch();

        assertThat(studentList.size()).isEqualTo(1);
        assertThat(studentList.get(0).getFirstName()).isEqualTo("John");

        pageIndex = 1;

        query = new JPAQuery<>(entityManager);
        studentList = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .offset(pageIndex * pageSize)
                .limit(pageSize)
                .fetch();

        assertThat(studentList.size()).isEqualTo(1);
        assertThat(studentList.get(0).getFirstName()).isEqualTo("Jane");

        pageIndex = 2;

        query = new JPAQuery<>(entityManager);
        studentList = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .offset(pageIndex * pageSize)
                .limit(pageSize)
                .fetch();

        assertThat(studentList.size()).isEqualTo(0);
    }

}
