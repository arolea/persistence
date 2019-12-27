package com.rolea.learning.orm.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "student")
@Getter
@Setter
public class Student {

	/**
	 * Sequences can be used in JDBC batches (relative to Identity generated ids, which can't)
	 * Sequences have a locking mechanism that happens outside the transactional context, increasing scalability
	 *
	 * Sequences assign batches of ids that the client uses offline (in order to avoid contention on db)
	 * The batch size (configured via allocationSize) should match the number of inserts in a transaction
	 *
	 * Sequences are the most efficient identifier choice, being highly optimized and integrating with batching
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_generator")
	@SequenceGenerator(name="student_generator", sequenceName = "student_seq")
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

	/**
	 * Big performance gain out of using Set instead of List for many to many (on entity removal)
	 */
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinTable(name = "student_course",
			joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "student_id"),
			inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "course_id"))
	private Set<Course> courses = new HashSet<>();

	/**
	 * Used only in transactional contexts (on managed entities) in order to sync entities
	 */

	public void addAddress(Address address){
		this.address = address;
		address.setStudent(this);
	}

	public void removeAddress(Address address){
		if(address != null){
			address.setStudent(null);
		}
		this.address = null;
	}

	public void addGrade(Grade grade){
		this.getGrades().add(grade);
		grade.setStudent(this);
	}

	public void removeGrade(Grade grade){
		this.getGrades().remove(grade);
		grade.setStudent(null);
	}

	public void addCourse(Course course){
		this.courses.add(course);
		course.getStudents().add(this);
	}

	public void removeCourse(Course course){
		this.courses.remove(course);
		course.getStudents().remove(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Student student = (Student) o;
		return Objects.equals(studentId, student.studentId) &&
				Objects.equals(firstName, student.firstName) &&
				Objects.equals(lastName, student.lastName) &&
				Objects.equals(address, student.address) &&
				Objects.equals(grades, student.grades) &&
				Objects.equals(courses, student.courses);
	}

	@Override
	public int hashCode() {
		return 31;
	}

}
