package edu.uob.Exceptions.Table;

import edu.uob.Exceptions.GenericException;

public class InvalidName extends GenericException {
    public InvalidName(){
        super("Invalid table name.");
    }
}