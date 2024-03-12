package edu.uob;

import edu.uob.Controller.Parser;
import edu.uob.Exceptions.Database.NotFound;
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

    @Test
    public void testUsingNonExistentDB() {
        DBServer s = new DBServer();
        assertThrows(NotFound.class, () -> new Parser("USE test;",s));
    }

    @Test
    public void testCreateTable() throws GenericException {
        DBServer s = new DBServer();
        // todo: this setup stuff is duplivative. Might want to move it somewhere.

        new Parser("CREATE DATABASE test;",s);
        new Parser("USE test;",s);
        new Parser("CREATE TABLE t;",s);
        new Parser("DROP TABLE t;",s);
        new Parser("CREATE TABLE marks (name, mark, pass);",s);
        assertNotNull(s.getCurrentDatabase());
        assertNotNull(s.getCurrentDatabase().getTableByName("marks"));
        String tableToString =  s.getCurrentDatabase().getTableByName("marks").toString();
        assertTrue(tableToString.contains("name\tmark\tpass"));
        new Parser("DROP DATABASE test;",s);
    }


    // TODO: test         // INSERT INTO marks VALUES ('Simon', 65, TRUE);

}
