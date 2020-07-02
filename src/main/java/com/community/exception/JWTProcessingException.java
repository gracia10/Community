package com.community.exception;

import org.springframework.security.core.AuthenticationException;

public class JWTProcessingException extends AuthenticationException{

	private static final long serialVersionUID = 1L;

	public JWTProcessingException(String msg, Throwable t) {
        super(msg, t);
    }

    public JWTProcessingException(String msg) {
        super(msg);
    }

}
