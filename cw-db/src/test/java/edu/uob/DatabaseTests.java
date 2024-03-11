package edu.uob;

import edu.uob.Exceptions.Database.AlreadyExists;
import edu.uob.Model.Database;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTests {
    @Test
    public void testDatabaseCreation(){
        // todo: better errors.
        Database d = new Database("a");
        assertDoesNotThrow(d::createDatabase,"Threw an exception and wasn't expected to.");
        Database e = new Database("a");
        assertThrows(AlreadyExists.class,e::createDatabase,"Expected an already exists error");
        assertDoesNotThrow(d::dropDatabase, "Threw an exception and wasn't expected to.");
    }
}
