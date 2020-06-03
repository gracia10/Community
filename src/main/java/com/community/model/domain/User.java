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
import com.community.model.Role;
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
	@Column(name = "user_email")
	private String email;
	
	@Column(name = "user_nm",nullable = false)
	private String name;
	
	@Column(name = "user_status", nullable = false)
	private boolean status;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_auth", nullable = false)
	private Role auth;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "user_platform", nullable = false)
	private AuthProvider provider;
	
	@JsonIgnore
	private LocalDateTime lastlogin;
	
	public void update() {
		this.lastlogin = LocalDateTime.now();
	}
	
	@Builder
	public User(String email,String name, boolean status, Role auth, AuthProvider provider) {
		this.email = email;
		this.name = name;
		this.status = status;
		this.auth = auth;
		this.provider = provider;
	}

}

//("user1@gmail.com","nick1","김철수","auth..인증권한?","naver","2020-05-26 00:00:00","2020-05-26 00:00:00","active","SO","180","r","foward","right",0,"user");
//("admin@gmail.com","nick4","김현수","auth","naver","2020-05-26 00:00:00","2020-05-26 00:00:00","active","JL","182","f","back","right",3,"admin");

