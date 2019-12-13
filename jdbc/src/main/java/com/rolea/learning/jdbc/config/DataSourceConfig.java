package com.rolea.learning.jdbc.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

	/**
	 * This usually gets configured by spring auto-configuration
	 * Creates a Hikari Data Source that supports connection pooling
	 * See https://github.com/brettwooldridge/HikariCP for all configuration options
	 */
	@Bean
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
		hikariConfig.addDataSourceProperty("URL", "jdbc:h2:mem:test");
		hikariConfig.addDataSourceProperty("user", "sa");
		hikariConfig.addDataSourceProperty("password", "sa");
		return new HikariDataSource(hikariConfig);
	}

	/**
	 * This usually gets configured by spring auto-configuration
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(){
		return new JdbcTemplate(dataSource());
	}

}
