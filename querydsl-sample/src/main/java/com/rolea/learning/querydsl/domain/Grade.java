package com.rolea.learning.querydsl.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "grade")
@Getter
@Setter
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grade_generator")
    @SequenceGenerator(name = "grade_generator", sequenceName = "grade_seq")
    @Column(name = "grade_id")
    private Long gradeId;

    private Double gradeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Student student;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade1 = (Grade) o;
        return gradeId != null &&
                Objects.equals(gradeId, grade1.gradeId) &&
                Objects.equals(gradeValue, grade1.gradeValue);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
