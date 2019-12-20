package com.rolea.learning.orm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "address")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"addressId"})
@ToString(of = {"addressId", "city", "street"})
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
