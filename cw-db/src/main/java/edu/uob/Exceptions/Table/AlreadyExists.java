package edu.uob.Exceptions.Table;

import edu.uob.Exceptions.GenericException;

public class AlreadyExists extends GenericException {
    public AlreadyExists(String name) {
        super("A table with the name " +name+ " already exists. Rename your table.");
    }
}


