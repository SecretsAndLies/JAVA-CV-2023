package edu.uob.Exceptions.Table;

import edu.uob.Exceptions.GenericException;

public class NotFound extends GenericException {
    public NotFound(String name){
        super("Table " +name+ " not found.");
    }
}
