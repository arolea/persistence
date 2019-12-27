package com.rolea.learning.orm;

import com.rolea.learning.orm.domain.Address;
import com.rolea.learning.orm.domain.Course;
import com.rolea.learning.orm.domain.Grade;
import com.rolea.learning.orm.domain.Student;
import com.rolea.learning.orm.repository.AddressRepository;
import com.rolea.learning.orm.repository.CourseRepository;
import com.rolea.learning.orm.repository.GradeRepository;
import com.rolea.learning.orm.repository.StudentRepository;
import com.rolea.learning.orm.service.StudentService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ORMTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentService studentService;

    @Test
    void oneToOneTest() {
        Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");

        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.addAddress(address);
        student = studentRepository.save(student);

        Optional<Student> studentOptional = studentRepository.findById(student.getStudentId());
        Optional<Address> addressOptional = addressRepository.findById(student.getAddress().getAddressId());

        assertThat(studentOptional.isPresent())
                .as("Student is properly fetched from the DB via its onw repository")
                .isTrue();
        studentOptional.ifPresent(stud -> assertThat(stud.getAddress())
                .as("Address is eagerly fetched")
                .isNotNull());
        assertThat(addressOptional.isPresent())
                .as("Address is properly fetched from the DB via its own repository")
                .isTrue();
    }

    @Test
    void oneToManyTest() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student = studentRepository.save(student);

        Grade firstGrade = new Grade();
        firstGrade.setGrade(7.5);
        firstGrade.setStudent(student);

        Grade secondGrade = new Grade();
        secondGrade.setGrade(8.2);
        secondGrade.setStudent(student);

        gradeRepository.saveAll(asList(firstGrade, secondGrade));

        Optional<Student> studentOptional = studentRepository.findById(student.getStudentId());

        // grades are not eagerly fetched
        assertThat(studentOptional.isPresent())
                .as("Student is properly fetched from the DB via its onw repository")
                .isTrue();
        studentOptional.ifPresent(stud ->
                assertThrows(LazyInitializationException.class, () -> stud.getGrades().size()));
        student = studentService.lazyLoadStudent(student.getStudentId());
        assertThat(student.getGrades().size())
                .as("Grades are lazily loaded via transactional service")
                .isEqualTo(2);
    }

    @Test
    public void testCascading() {
        Student student = studentService.cascadeInsertStudent();

        assertThat(student.getStudentId())
                .as("Student is properly saved")
                .isNotNull();

        Optional<Address> addressOptional = addressRepository.findByStudent(student);
        assertThat(addressOptional.isPresent())
                .as("Address is properly saved via cascading")
                .isTrue();

        List<Grade> grades = gradeRepository.findAllByStudent(student);
        assertThat(grades.size())
                .as("Grades as properly saved via cascading")
                .isEqualTo(2L);

        List<Course> courses = courseRepository.findAllByStudentsIn(singletonList(student));
        assertThat(courses.size())
                .as("Courses as properly saved via cascading")
                .isEqualTo(2L);

        student = studentService.lazyLoadStudent(student.getStudentId());
        assertThat(student.getAddress())
                .as("Address is lazily loaded")
                .isNotNull();
        assertThat(student.getCourses().size())
                .as("Courses are lazily loaded")
                .isEqualTo(2L);
        assertThat(student.getGrades().size())
                .as("Grades are lazily loaded")
                .isEqualTo(2L);

        student = studentService.cascadeDeleteStudent(student.getStudentId());
        student = studentService.lazyLoadStudent(student.getStudentId());
        assertThat(student.getAddress())
                .as("Address is null")
                .isNull();
        assertThat(student.getCourses().size())
                .as("Courses are lazily loaded")
                .isEqualTo(1L);
        assertThat(student.getGrades().size())
                .as("Grades are lazily loaded")
                .isEqualTo(1L);
    }

    @Test
    void manyToManyTest() {
        Course math = new Course();
        math.setCourseName("Math");
        math = courseRepository.save(math);

        Course biology = new Course();
        biology.setCourseName("Biology");
        biology = courseRepository.save(biology);

        Student john = new Student();
        john.setFirstName("John");
        john.setLastName("Doe");
        john = studentRepository.save(john);

        john.addCourse(math);
        john.addCourse(biology);
        john = studentRepository.save(john);

        Optional<Student> studentOptional = studentRepository.findById(john.getStudentId());

        assertThat(studentOptional.isPresent())
                .as("Student is properly fetched from the DB via its onw repository")
                .isTrue();
        studentOptional.ifPresent(stud ->
                assertThrows(LazyInitializationException.class, () -> stud.getCourses().size()));

        john = studentService.lazyLoadStudent(john.getStudentId());
        assertThat(john.getCourses().size())
                .as("Courses are lazily loaded via transactional service")
                .isEqualTo(2);
    }

}
