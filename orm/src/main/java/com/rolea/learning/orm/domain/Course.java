package com.rolea.learning.orm.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "course")
@Getter
@Setter
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_generator")
	@SequenceGenerator(name="course_generator", sequenceName = "course_seq")
	@Column(name = "course_id")
	private Long courseId;

	@Column(name = "course_name")
	private String courseName;

	@ManyToMany(mappedBy = "courses")
	private Set<Student> students = new HashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Course course = (Course) o;
		return Objects.equals(courseId, course.courseId);
	}

	@Override
	public int hashCode() {
		return 31;
	}
}
