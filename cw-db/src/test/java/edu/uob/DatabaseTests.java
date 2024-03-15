package edu.uob;

import edu.uob.Exceptions.Database.AlreadyExists;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Database.InvalidName;
import edu.uob.Exceptions.GenericException;
import edu.uob.Model.Database;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTests {
    @Test
    public void testDatabaseCreation() throws GenericException {
        // todo: better errors.
        Database d = new Database("a");
        assertDoesNotThrow(d::createDatabase,"Threw an exception and wasn't expected to.");
        Database e = new Database("A");
        assertThrows(AlreadyExists.class,e::createDatabase,"Expected an already exists error");
        assertDoesNotThrow(d::dropDatabase, "Threw an exception and wasn't expected to.");
        try {
            Database f = new Database("_non_alpha");
            fail("Attempted to create a db with non alpha numeric characters and didn't throw exception.");
        } catch (InvalidName fe) {
            // pass.
        }


    }
}
