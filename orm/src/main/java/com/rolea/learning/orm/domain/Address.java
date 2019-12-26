package com.rolea.learning.orm.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "address")
@Getter
@Setter
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Address address = (Address) o;
		return Objects.equals(addressId, address.addressId);
	}

	@Override
	public int hashCode() {
		return 31;
	}
}
