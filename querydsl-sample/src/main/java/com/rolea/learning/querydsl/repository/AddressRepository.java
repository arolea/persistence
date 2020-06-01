package com.rolea.learning.querydsl.repository;

import com.rolea.learning.querydsl.domain.Address;
import com.rolea.learning.querydsl.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByStudent(Student student);

}
