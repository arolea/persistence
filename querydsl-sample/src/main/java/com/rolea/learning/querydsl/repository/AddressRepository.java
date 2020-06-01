package com.rolea.learning.querydsl.repository;

import com.rolea.learning.querydsl.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
