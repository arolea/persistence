package com.rolea.learning.orm.repository;

import com.rolea.learning.orm.domain.Grade;
import com.rolea.learning.orm.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findAllByStudent(Student student);

}
