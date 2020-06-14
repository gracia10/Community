package com.community.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.community.model.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseTimeEntity{

	private static final long serialVersionUID = 1L;

	@Id
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "status", nullable = false)
	private boolean status;
	
	@Column(name = "privs", nullable = false)
	private String privs;
	
}

//	(MASTER, NOMAL)