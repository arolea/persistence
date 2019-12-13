package com.rolea.learning.jdbc.mapper;

import com.rolea.learning.jdbc.domain.Student;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StudentMapper implements RowMapper<Student> {

	@Override
	public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Student
				.builder()
				.id(rs.getLong("id"))
				.firstName(rs.getString("first_name"))
				.lastName(rs.getString("last_name"))
				.build();
	}

}
