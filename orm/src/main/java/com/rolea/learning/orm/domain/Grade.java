package com.rolea.learning.orm.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "grade")
@Getter
@Setter
@EqualsAndHashCode(of = {"gradeId"})
@ToString(of = {"gradeId", "grade"})
public class Grade {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grade_generator")
	@SequenceGenerator(name="grade_generator", sequenceName = "grade_seq")
	@Column(name = "grade_id")
	private Long gradeId;

	@Column(name = "grade")
	private Double grade;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "student_id")
	private Student student;

}
