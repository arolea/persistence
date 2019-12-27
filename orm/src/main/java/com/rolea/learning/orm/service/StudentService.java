package com.rolea.learning.orm.service;

import com.rolea.learning.orm.domain.Address;
import com.rolea.learning.orm.domain.Course;
import com.rolea.learning.orm.domain.Grade;
import com.rolea.learning.orm.domain.Student;
import com.rolea.learning.orm.repository.StudentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Log4j2
public class StudentService {

    @Autowired
    private StudentRepository repository;

    @Transactional
    public Student cascadeInsertStudent() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");

        // cascade one to one
        Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        student.addAddress(address);

        // cascade one to many
        Grade mathGrade = new Grade();
        mathGrade.setGrade(7.2);
        student.addGrade(mathGrade);
        Grade biologyGrade = new Grade();
        biologyGrade.setGrade(8.5);
        student.addGrade(biologyGrade);

        // cascade many to many
        Course math = new Course();
        math.setCourseName("Math");
        student.addCourse(math);
        Course biology = new Course();
        biology.setCourseName("Biology");
        student.addCourse(biology);

        return repository.save(student);
    }

    @Transactional
    public Student cascadeDeleteStudent(Long studentId) {
        return repository.findById(studentId)
                .map(student -> {
                    log.info("Lazy loading dependencies");
                    Address address = student.getAddress();
                    Set<Grade> grades = student.getGrades();
                    Set<Course> courses = student.getCourses();

                    log.info("Removing address");
                    student.removeAddress(address);

                    log.info("Removing a grade (if possible)");
                    if (grades.size() > 0) {
                        Grade gradeToRemove = grades.stream()
                                .findFirst()
                                .get();
                        student.removeGrade(gradeToRemove);
                    }

                    log.info("Removing course (if possible)");
                    if (courses.size() > 0) {
                        Course courseToRemove = courses.stream()
                                .findFirst()
                                .get();
                        student.removeCourse(courseToRemove);
                    }

                    return student;
                })
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Transactional
    public Student lazyLoadStudent(Long studentId) {
        return repository.findById(studentId)
                .map(student -> {
                    log.info("Triggering update on entity");
                    student.setFirstName("Jane");

                    log.info("Triggering lazy load for address");
                    log.info("Address {}", student.getAddress());

                    log.info("Triggering lazy load for grades");
                    log.info("Grades {}", student.getGrades());

                    log.info("Triggering lazy load for courses");
                    log.info("Courses {}", student.getCourses());

                    return student;
                })
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

}
