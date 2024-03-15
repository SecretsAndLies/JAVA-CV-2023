package edu.uob;

import edu.uob.Controller.Parser;
import edu.uob.Exceptions.Database.AlreadyExists;
import edu.uob.Exceptions.GenericException;
import edu.uob.Model.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    public void testCreateTable(){
        try {
            Table a = new Table("a", server.getCurrentDatabase());
            a.createTable();
        }catch (GenericException e) {
            fail("Threw an exception while creating a table.");
        }
        try {
            Table b = new Table("a?", server.getCurrentDatabase());
            fail("Didn't get an error when attempting to create a non alphanumeric table.");
        }catch (GenericException e) {
            // pass
        }

        try {
            Table c = new Table("A", server.getCurrentDatabase());
            c.createTable();
            fail("Didn't get an error when attempting to create a non duplicate named table (uppercase).");
        }catch (GenericException e) {
            // pass
        }

    }
}
