package com.rolea.learning.jdbc;

import com.rolea.learning.jdbc.mapper.StudentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JDBCTests {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void contextLoads(){
		assertThat(dataSource)
				.as("DataSource is properly initialized")
				.isNotNull();

		assertThat(jdbcTemplate)
				.as("JdbcTemplate is properly initialized")
				.isNotNull();

		jdbcTemplate
				.query("select id, first_name, last_name from student", new StudentMapper())
				.forEach(System.out::println);
	}



}
