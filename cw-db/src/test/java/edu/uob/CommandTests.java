package edu.uob;

import edu.uob.Controller.Parser;
import edu.uob.Exceptions.GenericException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTests {
    @Test
    public void testCreateUseDelete() throws GenericException {
        DBServer s = new DBServer();

         new Parser("CREATE DATABASE test;",s);
         new Parser("USE test;",s);
         assertNotNull(s.getCurrentDatabase());
         assertEquals(s.getCurrentDatabase().getName(), "test");
         new Parser("DROP DATABASE test;",s);
         assertNull(s.getCurrentDatabase());
    }
}
