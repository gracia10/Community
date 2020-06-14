package com.community.model.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.community.model.AuthProvider;
import com.community.model.Authority;
import com.community.model.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements Serializable{
	
	private static final long serialVersionUID = 8190306758916033655L;

	@Id
	@Column(name = "user_id")
	private String id;
	
	@Column(name = "user_nm",nullable = false)
	private String name;
	
	@JsonIgnore
	@Column(name = "user_status", nullable = false)
	private boolean status;
	
	@JsonIgnore
	@Enumerated(EnumType.STRING)
	@Column(name = "user_auth", nullable = false)
	private Authority authorities;

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	@Column(name = "user_platform", nullable = false)
	private AuthProvider provider;

	@JsonIgnore
	private LocalDateTime lastlogin = LocalDateTime.now();
	
	public void update() {
		this.lastlogin = LocalDateTime.now();
	}
	
	@Builder
	public User(String id,String name, boolean status, Authority authorities, AuthProvider provider) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.authorities = authorities;
		this.provider = provider;
	}

}

//"admin@gmail.com","김현수", true , "admin", "naver", "2020-05-26 00:00:00","2020-05-26 00:00:00");
