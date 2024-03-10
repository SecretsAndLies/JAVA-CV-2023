package edu.uob.Exceptions.Database;

import edu.uob.Exceptions.GenericException;

public class NotFound extends GenericException {
    public NotFound(String databaseName){
        super("Database " +databaseName+ " not found.");
    }
}
