package com.rolea.learning.querydsl.repository;

import com.rolea.learning.querydsl.domain.Student;
import com.rolea.learning.querydsl.repository.custom.StudentAggregationRepository;
import com.rolea.learning.querydsl.repository.custom.StudentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentRepositoryCustom, StudentAggregationRepository {

}
