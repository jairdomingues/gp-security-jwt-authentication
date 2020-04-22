package com.bezkoder.springjwt.exception;

public class CustomGenericUnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CustomGenericUnauthorizedException() {
        super();
    }

    public CustomGenericUnauthorizedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CustomGenericUnauthorizedException(final String message) {
        super(message);
    }

    public CustomGenericUnauthorizedException(final Throwable cause) {
        super(cause);
    }
}
