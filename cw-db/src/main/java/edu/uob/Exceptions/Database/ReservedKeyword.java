package edu.uob.Exceptions.Database;

import edu.uob.Exceptions.GenericException;

public class ReservedKeyword extends GenericException {
    public ReservedKeyword(String databaseName){
        super("You cannot call a database "+ databaseName + " because it is a reserved keyword in SQL.");
    }
}
