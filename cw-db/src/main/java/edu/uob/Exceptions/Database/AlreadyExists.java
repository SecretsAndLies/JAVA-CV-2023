package edu.uob.Exceptions.Database;

import edu.uob.Exceptions.GenericException;

public class AlreadyExists extends GenericException {
    public AlreadyExists(String databaseName){
        super("A database with the name " +databaseName+ " already exists. Rename your database.");
    }
}
