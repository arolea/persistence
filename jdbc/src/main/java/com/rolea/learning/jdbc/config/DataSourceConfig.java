package com.rolea.learning.jdbc.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

/**
 * This usually gets configured by spring auto-configuration
 */
@Configuration
public class DataSourceConfig {

	/**
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
	 * Uses PreparedStatement under the hood in order to defend against SQL injection
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(){
		return new JdbcTemplate(dataSource());
	}

}
