package org.ericwubbo.restcontrolleradvicedemo;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
