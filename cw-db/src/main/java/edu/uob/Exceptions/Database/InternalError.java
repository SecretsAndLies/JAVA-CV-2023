package edu.uob.Exceptions.Database;

import edu.uob.Exceptions.GenericException;

public class InternalError extends GenericException {
    public InternalError() {
        super("Can't complete this action due to an internal error.");
    }

    public InternalError(String error) {
        super("Can't complete this action due to an internal error. " + error);
    }

}
