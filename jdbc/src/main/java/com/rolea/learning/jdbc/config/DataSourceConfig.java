package com.rolea.learning.jdbc.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * This usually gets configured by spring auto-configuration
 *
 * Query execution passes through three modules
 * 1. Parser - ensures the SQL is valid and afterwards converts it to the parse tree / query tree
 * 2. Optimizer - analyzes the query tree and decides on the most efficient access plan (indexes to use, scan, etc)
 * 3. Executor - fetched the data based on the access plan received from the optimizer
 *
 * Execution plan performance is based on the cardinality of the given bind parameters.
 * The more common a value is the more likely a table scan makes sense.
 * The less common an indexed value is, the more an index in that column makes sense.
 *
 * Server-side prepared statements caching allow access plan reuse between executions with different parameters.
 * Client-side prepared statements caching reduce the processing on client side.
 * The client-side cache is generally specific to each connection.
 *
 * Postgres can cache multiple plans for multiple sets of bind parameters for a single prepared statement.
 * Postgres falls back to a default generic plan if the performance gains of caching multiple plans are low.
 */
@Configuration
public class DataSourceConfig {

	@Autowired
	private PlatformTransactionManager transactionManager;

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
	 * Uses PreparedStatement under the hood in order to defend against SQL injection and benefit out of caching
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(){
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public TransactionTemplate transactionTemplate(){
		return new TransactionTemplate(transactionManager);
	}

}
