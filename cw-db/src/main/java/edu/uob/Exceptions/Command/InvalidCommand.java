package edu.uob.Exceptions.Command;

import edu.uob.Exceptions.GenericException;

public class InvalidCommand extends GenericException {
    public InvalidCommand() {
        super("Your command is invalid. ");
    }

    public InvalidCommand(String errorMessage) {
        super("Your command is invalid. " + errorMessage);
    }
}
