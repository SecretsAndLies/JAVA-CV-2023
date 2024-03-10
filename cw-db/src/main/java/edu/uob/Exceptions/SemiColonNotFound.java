package edu.uob.Exceptions;

public class SemiColonNotFound extends GenericException {
    public SemiColonNotFound(String databaseName){
        super("Database " +databaseName+ " not found.");
    }
}
