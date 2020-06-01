package com.rolea.learning.querydsl;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.rolea.learning.querydsl.domain.QStudent;
import com.rolea.learning.querydsl.domain.Student;
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
    private JPAQuery<Student> query;

    @BeforeEach
    public void setUp(){
        dataUtils.populateData();
    }

    @AfterEach
    public void tearDown(){
        dataUtils.cleanData();
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
        log.info("Testing fetch by student name");

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

        log.info("Finished test");
    }

    /**
     * Examples of fetching students by their city (student -> address -> city)
     */
    @Test
    public void testFetchByAddressCity() {
        log.info("Testing fetch by address city");

        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .where(qStudent.address.city.eq("City"))
                .fetch();

        // test all relevant students are fetched
        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            // test all relevant data is properly fetched for each student
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
        });

        log.info("Finished test");
    }

    /**
     * Example of fetching students associated with a given grade
     */
    @Test
    public void testFetchByGradeValue() {
        log.info("Testing fetching all students that have a given grade");

        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                // required to fetch the address in the same query as the student
                .innerJoin(qStudent.address).fetchJoin()
                // required to fetch the grades in the same query as the student
                // without this, grades are lazily loaded
                .innerJoin(qStudent.grades).fetchJoin()
                .where(qStudent.grades.any().grade.eq(10D))
                .distinct()
                .fetch();

        // test all relevant students are fetched
        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            // test all relevant data is properly fetched for each student
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
            currentStudent.getGrades().forEach(currentGrade -> assertThat(currentGrade.getGradeId()).isNotNull());
        });

        log.info("Finished test");
    }

    /**
     * Example of fetching students associated with a given course
     */
    @Test
    public void testFetchByCourseName() {
        log.info("Testing fetch all students that are registered to a given course");

        query = new JPAQuery<>(entityManager);
        List<Student> studentList = query.from(qStudent)
                // required to fetch the address in the same query as the student
                .innerJoin(qStudent.address).fetchJoin()
                // required to fetch the grades in the same query as the student
                // without this, grades are lazily loaded
                .innerJoin(qStudent.grades).fetchJoin()
                // required to fetch the courses in the same query as the student
                // without this, courses are lazily loaded
                .innerJoin(qStudent.courses).fetchJoin()
                .where(qStudent.courses.any().courseName.eq("First"))
                .distinct()
                .fetch();

        // test all relevant students are fetched
        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            // test all relevant data is properly fetched for each student
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
            currentStudent.getGrades().forEach(currentGrade -> assertThat(currentGrade.getGradeId()).isNotNull());
            currentStudent.getCourses().forEach(currentCourse -> assertThat(currentCourse.getCourseId()).isNotNull());
        });

        log.info("Finished test");
    }

    /**
     * Fetch all the students associated with ALL of the given courses
     */
    @Test
    public void testFetchMatchingAllCourses() {
        log.info("Testing fetch all students that are registered to a given course");

        List<String> courseList = List.of("First", "Third");
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

        log.info("Finished test");
    }

    /**
     * Fetch all the students associated with ANY of the given courses
     */
    @Test
    public void testFetchMatchingAnyCourses() {
        log.info("Testing fetch all students that are registered to a given course");

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

        log.info("Finished test");
    }

    @Test
    public void testGroupByGradeValue() {
        log.info("Testing grouping students by grade values");

        log.info("Grouping by on application side");
        query = new JPAQuery<>(entityManager);
        Map<Double, List<Student>> studentMap = query.from(qStudent)
                .innerJoin(qStudent.address).fetchJoin()
                .transform(GroupBy.groupBy(qStudent.grades.any().grade).as(GroupBy.list(qStudent)));

        // test all relevant students are fetched
        assertThat(studentMap.size()).isEqualTo(2);
        assertThat(studentMap.get(10D).size()).isEqualTo(2);
        assertThat(studentMap.get(5D).size()).isEqualTo(1);

        log.info("Finished test");
    }

}
