package com.rolea.learning.orm.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
@Getter
@Setter
@EqualsAndHashCode(of = {"courseId"})
@ToString(of = {"courseId", "courseName"})
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

}
