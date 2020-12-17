package com.rolea.learning.querydsl;

import com.rolea.learning.querydsl.domain.Student;
import com.rolea.learning.querydsl.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest(properties = {""})
@TestPropertySource(value = "classpath:persistence-test.properties")
@Sql(scripts = "classpath:test-data.sql")
@Slf4j
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void test_findByFirstName_success() {
        Student student = studentRepository.findByFirstName("John")
                .orElseThrow(() -> new RuntimeException("Student not found"));

        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Doe");
        assertThat(student.getAddress().getAddressId()).isNotNull();
    }

    @Test
    public void test_findByLastName_success() {
        List<Student> studentList = studentRepository.findByLastName("Doe");

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
        });
    }

    @Test
    public void test_findByCity_success() {
        List<Student> studentList = studentRepository.findByCity("City");

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
        });
    }

    @Test
    public void test_findByGrade_success() {
        List<Student> studentList = studentRepository.findByGrade(10D);

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
            currentStudent.getGrades().forEach(currentGrade -> assertThat(currentGrade.getGradeId()).isNotNull());
        });
    }

    @Test
    public void test_findByCourse_success() {
        List<Student> studentList = studentRepository.findByCourse("First");

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
            currentStudent.getGrades().forEach(currentGrade -> assertThat(currentGrade.getGradeId()).isNotNull());
            currentStudent.getCourses().forEach(currentCourse -> assertThat(currentCourse.getCourseId()).isNotNull());
        });
    }

    @Test
    public void test_findByHavingAllCourses_success() {
        List<Student> studentList = studentRepository.findByAllCourses(List.of("First", "Second"));

        assertThat(studentList.size()).isEqualTo(1);
    }

    @Test
    public void test_findByHavingAnyCourses_success() {
        List<Student> studentList = studentRepository.findByAnyCourses(List.of("First", "Second"));

        assertThat(studentList.size()).isEqualTo(2);
    }

    @Test
    public void test_findAll_success() {
        List<Student> studentList = studentRepository.findAll(0, 1);

        assertThat(studentList.size()).isEqualTo(1);
        assertThat(studentList.get(0).getFirstName()).isEqualTo("John");

        studentList = studentRepository.findAll(1, 1);

        assertThat(studentList.size()).isEqualTo(1);
        assertThat(studentList.get(0).getFirstName()).isEqualTo("Jane");

        studentList = studentRepository.findAll(2, 1);

        assertThat(studentList.size()).isEqualTo(0);
    }

    @Test
    public void test_count_success() {
        long studentsCount = studentRepository.countStudents();

        assertThat(studentsCount).isEqualTo(2);
    }

    @Test
    public void test_groupStudentsByGrades_success() {
        Map<Double, List<Student>> studentMap = studentRepository.groupStudentsByGrades();

        assertThat(studentMap.size()).isEqualTo(2);
        assertThat(studentMap.get(10D).size()).isEqualTo(2);
        assertThat(studentMap.get(5D).size()).isEqualTo(1);
    }

    @Test
    public void test_computeGradeAveragePerStudent_success() {
        Map<Long, Double> gradeAverage = studentRepository.computeGradeAveragePerStudent();

        assertThat(gradeAverage.values().containsAll(List.of(7.5D, 10D))).isTrue();
    }

    @Test
    public void test_groupStudentsByCourses_success() {
        Map<String, List<Student>> courseMap = studentRepository.groupStudentsByCourses();

        assertThat(courseMap.get("First").size()).isEqualTo(2);
        assertThat(courseMap.get("Second").size()).isEqualTo(1);
        assertThat(courseMap.get("Third").size()).isEqualTo(1);
    }

    @Test
    public void test_computeStudentCountPerCourse_success() {
        Map<String, Long> courseStudentCount = studentRepository.computeStudentCountPerCourse();

        assertThat(courseStudentCount.get("First")).isEqualTo(2L);
        assertThat(courseStudentCount.get("Second")).isEqualTo(1L);
        assertThat(courseStudentCount.get("Third")).isEqualTo(1L);
    }

    @Test
    public void test_findByCourseSubQuery_success() {
        List<Student> studentList = studentRepository.findByCourseSubQuery("First");

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getAddress().getAddressId()).isNotNull();
            currentStudent.getGrades().forEach(currentGrade -> assertThat(currentGrade.getGradeId()).isNotNull());
            currentStudent.getCourses().forEach(currentCourse -> assertThat(currentCourse.getCourseId()).isNotNull());
        });
    }

    @Test
    public void test_findStudentIdsForCourse_success() {
        List<Long> studentIds = studentRepository.findStudentIdsByCourse("First");

        assertThat(studentIds).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    public void test_findByProjectionForCourse_success() {
        List<Student> studentList = studentRepository.findStudentProjectionByCourse("First");

        assertThat(studentList.size()).isEqualTo(2);
        studentList.forEach(currentStudent -> {
            assertThat(currentStudent.getStudentId()).isNotNull();
            assertThat(currentStudent.getFirstName()).isNotNull();
            assertThat(currentStudent.getAddress()).isNull();
            assertThat(currentStudent.getGrades()).isEmpty();
            assertThat(currentStudent.getCourses()).isEmpty();
        });
    }

}
