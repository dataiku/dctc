package com.dataiku.dctc;

import java.io.IOException;

public class AuthenticationFailedException extends IOException{
    private static final long serialVersionUID = 1L;

    public AuthenticationFailedException(String message) {
        super(message);
    }
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
