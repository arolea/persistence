package com.rolea.learning.orm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student")
@Getter
@Setter
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "student_id")
	private Long studentId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	// @JoinColumn only on the owning side of the one to one relationship
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "address_id", referencedColumnName = "address_id")
	private Address address;

	@OneToMany(mappedBy = "student")
	private Set<Grade> grades = new HashSet<>();

	// @JoinTable only on the owning side of the one to one relationship
	@ManyToMany()
	@JoinTable(name = "student_course",
			joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "student_id"),
			inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "course_id"))
	private Set<Course> courses = new HashSet<>();

}
