package com.rolea.learning.querydsl;

import com.rolea.learning.querydsl.domain.Address;
import com.rolea.learning.querydsl.domain.Course;
import com.rolea.learning.querydsl.domain.Grade;
import com.rolea.learning.querydsl.domain.Student;
import com.rolea.learning.querydsl.repository.CourseRepository;
import com.rolea.learning.querydsl.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataUtils {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * John Doe -> 2 grades, 2 courses (First, Second)
     * Jane Doe -> 1 grade, 2 courses (First, Third)
     */
    @Transactional
    public void populateData(){
        Student firstStudent = new Student();
        firstStudent.setFirstName("John");
        firstStudent.setLastName("Doe");

        Address firstStudentAddress = new Address();
        firstStudentAddress.setCity("City");
        firstStudentAddress.setStreet("Street");
        firstStudent.addAddress(firstStudentAddress);

        Grade firstStudentFirstGrade = new Grade();
        firstStudentFirstGrade.setGrade(10D);
        Grade firstStudentSecondGrade = new Grade();
        firstStudentSecondGrade.setGrade(5D);
        firstStudent.addGrade(firstStudentFirstGrade);
        firstStudent.addGrade(firstStudentSecondGrade);

        Course firstCourse = new Course();
        firstCourse.setCourseName("First");
        Course secondCourse = new Course();
        secondCourse.setCourseName("Second");
        firstStudent.addCourse(firstCourse);
        firstStudent.addCourse(secondCourse);

        studentRepository.save(firstStudent);

        Student secondStudent = new Student();
        secondStudent.setFirstName("Jane");
        secondStudent.setLastName("Doe");

        Address secondStudentAddress = new Address();
        secondStudentAddress.setCity("City");
        secondStudentAddress.setStreet("Street");
        secondStudent.addAddress(secondStudentAddress);

        Grade secondStudentFirstGrade = new Grade();
        secondStudentFirstGrade.setGrade(10D);
        secondStudent.addGrade(secondStudentFirstGrade);

        firstCourse = firstStudent.getCourses().stream()
                .filter(course -> course.getCourseName().equals("First"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Course not found"));
        secondStudent.addCourse(firstCourse);
        Course thirdCourse = new Course();
        thirdCourse.setCourseName("Third");
        secondStudent.addCourse(thirdCourse);

        studentRepository.save(secondStudent);
    }

    public void cleanData() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }
}
