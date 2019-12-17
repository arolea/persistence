package com.rolea.learning.jdbc.service.impl;

import com.rolea.learning.jdbc.domain.Student;
import com.rolea.learning.jdbc.mapper.StudentMapper;
import com.rolea.learning.jdbc.service.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Component
public class StudentDAOImpl implements StudentDAO {

	private static final int BATCH_SIZE = 100;
	private static final String SQL_INSERT = "INSERT INTO student(`first_name`, `last_name`) VALUES (?, ?)";
	private static final String SQL_FIND_ALL = "SELECT `id`, `first_name`, `last_name` FROM student";
	private static final String SQL_FIND_ONE = "SELECT `id`, `first_name`, `last_name` FROM student WHERE id = ?";
	private static final String SQL_DELETE_ONE = "DELETE FROM student WHERE id = ?";
	private static final String SQL_COUNT = "SELECT COUNT(*) FROM student";

	/**
	 * execute(...) methods are used for DDL statements
	 */
	@Autowired
	private JdbcTemplate template;

	@Override
	public Optional<Student> findOne(Long id) {
		return ofNullable(template.queryForObject(SQL_FIND_ONE, new Object[]{id}, new StudentMapper()));
	}

	@Override
	public List<Student> findAll() {
		return template.query(SQL_FIND_ALL, new StudentMapper());
	}

	@Override
	public Long save(Student student) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(SQL_INSERT, new String[] {"id"});
			ps.setString(1, student.getFirstName());
			ps.setString(2, student.getLastName());
			return ps;
		}, keyHolder);
		return requireNonNull(keyHolder.getKey()).longValue();
	}

	/**
	 * Batching reduces the number of trips to the db, increasing performance
	 */
	@Override
	public void saveAll(List<Student> students) {
		template.batchUpdate(SQL_INSERT, students, BATCH_SIZE, (ps, student) -> {
			ps.setString(1, student.getFirstName());
			ps.setString(2, student.getLastName());
		});
	}

	@Override
	public void delete(Long id) {
		template.update(SQL_DELETE_ONE, id);
	}

	@Override
	public Long count() {
		return template.queryForObject(SQL_COUNT, Long.class);
	}

}
