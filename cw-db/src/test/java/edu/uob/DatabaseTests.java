package edu.uob;

import edu.uob.Exceptions.Database.AlreadyExists;
import edu.uob.Model.Database;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTests {
    @Test
    public void testDatabaseCreation(){
        Database d = new Database("a");
        assertDoesNotThrow(d::createDatabase);
        Database e = new Database("a");
        assertThrows(AlreadyExists.class,e::createDatabase);
        assertDoesNotThrow(d::dropDatabase);
    }
}
