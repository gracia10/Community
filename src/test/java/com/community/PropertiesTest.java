package com.community;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.community.config.AppProperties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppProperties.class)
@EnableConfigurationProperties
public class PropertiesTest {

	@Autowired
	private AppProperties application;
	
	@Value("${application.auth.tokenSecret}")
	String tokenSecret;
	
    @Test
    public void whenCreatingUser_thenCreated() {
       System.out.println(application.getAuth().getTokenSecret());
       System.out.println(tokenSecret);
    }
}
