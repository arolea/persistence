package com.rolea.learning.querydsl.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "student")
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_generator")
    @SequenceGenerator(name = "student_generator", sequenceName = "student_seq", allocationSize = 50, initialValue = 1)
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Grade> grades = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "course_id"))
    private Set<Course> courses = new HashSet<>();

    @ElementCollection
    private List<Long> studentPhotoIds;

    public void addAddress(Address address) {
        this.address = address;
        address.setStudent(this);
    }

    public void removeAddress(Address address) {
        if (address != null) {
            address.setStudent(null);
        }
        this.address = null;
    }

    public void addGrade(Grade grade) {
        // this triggers lazy loading
        this.getGrades().add(grade);
        grade.setStudent(this);
    }

    public void removeGrade(Grade grade) {
        // this triggers lazy loading
        this.getGrades().remove(grade);
        grade.setStudent(null);
    }

    public void addCourse(Course course) {
        this.courses.add(course);
        // this triggers lazy loading
        course.getStudents().add(this);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
        // this triggers lazy loading
        course.getStudents().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentId != null &&
                Objects.equals(studentId, student.studentId) &&
                Objects.equals(firstName, student.firstName) &&
                Objects.equals(lastName, student.lastName);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
