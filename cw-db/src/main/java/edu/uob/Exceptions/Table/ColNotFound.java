package edu.uob.Exceptions.Table;

import edu.uob.Exceptions.GenericException;

public class ColNotFound extends GenericException {

    public ColNotFound() {
        super("Column not found.");
    }
}
