package com.community.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Authority {
	ROLE_GEUST,
	ROLE_ADMIN,
    ROLE_USER;
}