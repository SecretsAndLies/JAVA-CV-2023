package edu.uob.Exceptions.Table;

import edu.uob.Exceptions.GenericException;

public class InsertionError extends GenericException {
    public InsertionError(String message) {
        super(message);
    }
}
