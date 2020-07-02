package com.community;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisTest {
	
    @Test
    public void whenCreatingUser_thenCreated() throws JSONException {
    	String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    	String body = token.split("\\.")[1];
    	String jsonString = new String(Base64.getUrlDecoder().decode(body));
    	
    	Date date = new Date();
    	long time = 1000;
    	
    	System.out.println(System.currentTimeMillis());
    	System.out.println(date.getTime());
    	System.out.println(date.getTime()+time);
    	
    	Object obj = date.getTime()+time;
    	System.out.println(new Date((long)obj).getTime());
    	
    	
    	ObjectMapper mapper = new ObjectMapper();
    	try {
			Map<String, String> payload = mapper.readValue(jsonString, Map.class);
			System.out.println(payload);
			
    	} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

    }
}
