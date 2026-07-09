package com.artur.jobaggregator.project.exception.badrequest;

public class InvalidTokenException extends BadRequestException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
