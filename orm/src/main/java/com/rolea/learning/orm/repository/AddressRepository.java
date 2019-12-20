package com.rolea.learning.orm.repository;

import com.rolea.learning.orm.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
