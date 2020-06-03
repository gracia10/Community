package com.community.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	
	ADMIN("ROLE_ADMIN", "관리자"),
	USER("ROLE_USER", "사용자"),
	GUSET("ROLE_GUEST", "게스트");
	
	private final String role;
	private final String roleName;
}
