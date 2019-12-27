package com.rolea.learning.orm.repository;

import com.rolea.learning.orm.domain.Address;
import com.rolea.learning.orm.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByStudent(Student student);

}
