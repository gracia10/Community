package com.community.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

//@Configuration
@PropertySource({ "classpath:persistence-multiple-db-boot.properties" })
public class JpaConfig {

	@Bean
	@ConfigurationProperties(prefix = "alert.datasource")
	public DataSource userDataSource() {
		DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}
	
}
