package com.community.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.community.common.AuthConstants;
import com.community.common.Authority;
import com.community.config.AppProperties;
import com.community.exception.BadRequestException;
import com.community.exception.JWTProcessingException;
import com.community.model.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenProvider {

	public static final String BLACKLIST_NAME = "blackList";
	
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;
	
	@Autowired
	private AppProperties appProperties;
	
	public String createToken() {
		Date now = new Date();
		String token = Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.setExpiration(new Date(now.getTime() + appProperties.getAuth().getRefreshtokenExpirationMsec()))
				.signWith(SignatureAlgorithm.HS256, appProperties.getAuth().getTokenSecret())
				.compact();
		return token;
	}
	
    public String createToken(Map<String, Object> claims){
    	
    	Date exp = new Date((Long)claims.get(AuthConstants.CLAIM_EXPIRATION));
    	claims.remove(AuthConstants.CLAIM_EXPIRATION);
        String token = Jwts.builder()
        		.setClaims(claims)
        		.setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, appProperties.getAuth().getTokenSecret())
                .compact();
        return token;
    }
	
    public Claims getClaimsFromToken(String token) {
    	return Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
    }
    
    public User getUserFromToken(String token) {
    	Claims claims = getClaimsFromToken(token);
    	return User.builder()
    			.id(claims.getSubject())
    			.authorities(Authority.valueOf(claims.get(AuthConstants.CLAIM_ROLE, String.class)))
    			.status(claims.get(AuthConstants.CLAIM_STATUS, Boolean.class))
    			.build();
    }
    
    public Map<String,Object> getClaimsFromDecodToken(String token){
    	ObjectMapper mapper = new ObjectMapper();
    	String jsonString = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
    	Map<String, Object> map = null;
		try {
			map = mapper.readValue(jsonString, Map.class);
		} catch (Exception e) {
			log.error("convert JSON String to Map");
			throw new BadRequestException("convert JSON String to Map :"+e.getMessage());
		}
    	return map;
    }
    
    public boolean validateToken(String authToken) throws Exception{
        try {
        	Claims claims = getClaimsFromToken(authToken);
        	String jti = claims.getId();
        	if(hasKeyFromRedis(jti)) {
        		throw new ExpiredJwtException(null, null, "Expired JWT token");
        	}
            return true;
        } catch (ExpiredJwtException e) {
        	log.error("Expired JWT token :"+e.getClaims().getId()+e.getClaims().getExpiration());
        	throw new JWTProcessingException("Expired JWT token");
        } catch (SignatureException e) {
        	log.error("Invalid JWT signature");
        	throw new JWTProcessingException("Invalid JWT signature");
        } catch (MalformedJwtException e) {
        	log.error("Invalid JWT token");
        	throw new JWTProcessingException("Invalid JWT token");
        } catch (UnsupportedJwtException e) {
        	log.error("Unsupported JWT token");
        	throw new JWTProcessingException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
        	log.error("JWT claims string is empty. Maybe Empty Refresh Token");
            throw new IllegalArgumentException("JWT claims string is empty. Maybe Empty Refresh Token");
        }
    }
    
	public void setIdBlackList(String jti,long time) {
        redisTemplate.opsForValue().set(jti, "");
        redisTemplate.expire(jti, time, TimeUnit.SECONDS);
	}
	
    public boolean hasKeyFromRedis(String jti) {
    	return redisTemplate.hasKey(jti);
    }
}