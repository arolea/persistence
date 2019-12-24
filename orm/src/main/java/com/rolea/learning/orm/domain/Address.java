package com.rolea.learning.orm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@EqualsAndHashCode(of = {"addressId"})
@ToString(of = {"addressId", "city", "street"})
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_generator")
	@SequenceGenerator(name="address_generator", sequenceName = "address_seq")
	@Column(name = "address_id")
	private Long addressId;

	@Column(name = "street")
	private String street;

	@Column(name = "city")
	private String city;

	// no @JoinColumn on the managed side of the one to one relationship
	// adding a join column here would create a bi-directional relationship
	@OneToOne(mappedBy = "address")
	private Student student;

}
