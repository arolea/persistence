package com.rolea.learning.querydsl.domain;

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
    private Long addressId;

    private String street;
    private String city;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Student student;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return addressId != null &&
                Objects.equals(addressId, address.addressId) &&
                Objects.equals(street, address.street) &&
                Objects.equals(city, address.city);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
