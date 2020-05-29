package com.app.ws.exceptions;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = -5906991881954330793L;

    public UserServiceException(String message) {
        super(message);
    }
}
