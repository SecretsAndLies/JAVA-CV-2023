package edu.uob;

import java.io.Serializable;

public class GameException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;  // You can generate a specific version UID, but 1L is a common practice for simplicity

    public GameException(String errorMessage) {
        super(errorMessage);
    }
}
