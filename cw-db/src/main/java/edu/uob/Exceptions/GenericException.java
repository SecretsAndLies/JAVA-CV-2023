package edu.uob.Exceptions;


public class GenericException extends Exception {
    private final String errorMessage;
    public GenericException(String errorMessage) {
        super(errorMessage);
        this.errorMessage=errorMessage;
    }

    @Override
    public String toString() {
        return "[ERROR]: " + this.errorMessage;
    }
}
