package edu.uob;

import edu.uob.Controller.Parser;
import edu.uob.Exceptions.Database.AlreadyExists;
import edu.uob.Exceptions.GenericException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class TableTests {
    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        try {
            new Parser("CREATE DATABASE test;",server);
            new Parser("USE test;",server);
        } catch (GenericException e) {
            fail("Threw an exception while setting up the server.");
        }
    }

    @AfterEach
    public void tearDown(){
        try {
            new Parser("DROP DATABASE test;",server);
        } catch (GenericException e) {
            fail("Threw an exception while tearing down the server.");
        }
    }

    public void createTable(){
        // todo: should you test it here?
    }
}
