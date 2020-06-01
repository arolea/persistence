package com.rolea.learning.querydsl.repository;

import com.rolea.learning.querydsl.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {

}
