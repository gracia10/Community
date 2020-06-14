package com.community.exception;

import org.springframework.security.core.AuthenticationException;

public class BadRequestException extends AuthenticationException{

	private static final long serialVersionUID = 7515070977129978844L;

	public BadRequestException(String msg, Throwable t) {
        super(msg, t);
    }

    public BadRequestException(String msg) {
        super(msg);
    }

}
