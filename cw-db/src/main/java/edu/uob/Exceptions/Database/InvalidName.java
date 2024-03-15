package edu.uob.Exceptions.Database;

import edu.uob.Exceptions.GenericException;

public class InvalidName extends GenericException {
    public InvalidName(){
        super("Invalid database name.");
    }
}
