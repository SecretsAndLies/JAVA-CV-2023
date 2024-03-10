package edu.uob;

import edu.uob.Controller.CommandHandler;
import edu.uob.Exceptions.Database.AlreadyExists;
import edu.uob.Exceptions.GenericException;
import edu.uob.Model.Database;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTests {
    @Test
    public void testCreateUseDelete() throws GenericException {
        DBServer s = new DBServer();

         new CommandHandler("CREATE DATABASE test;",s);
         new CommandHandler("USE test;",s);
         assertNotNull(s.getCurrentDatabase());
         assertEquals(s.getCurrentDatabase().getName(), "test");
         new CommandHandler("DROP DATABASE test;",s);
         assertNull(s.getCurrentDatabase());
    }
}
