package edu.uob.Exceptions.Command;

import edu.uob.Exceptions.GenericException;

public class SemiColonNotFound extends GenericException {
    public SemiColonNotFound(String query){
        super("Your query must end with a semicolon. Did you mean: \n"+query+";");
    }
}
