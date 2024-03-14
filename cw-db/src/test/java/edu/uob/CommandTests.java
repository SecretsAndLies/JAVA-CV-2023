package edu.uob;

import edu.uob.Controller.Parser;
import edu.uob.Exceptions.Database.NotFound;
import edu.uob.Exceptions.GenericException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTests {
    // todo: switch to random names to avoid duplicate weirdness.
    @Test
    public void testCreateUseDelete() throws GenericException {
        DBServer s = new DBServer();

        new Parser("CREATE DATABASE test;", s);
        new Parser("USE test;", s);
        assertNotNull(s.getCurrentDatabase());
        assertEquals(s.getCurrentDatabase().getName(), "test");
        new Parser("DROP DATABASE test;", s);
        assertNull(s.getCurrentDatabase());
    }

    @Test
    public void persistAfterRestart() {
        DBServer s = new DBServer();
        String randomName = "Test";
        s.handleCommand("CREATE DATABASE " + randomName + ";");
        s.handleCommand("USE " + randomName + ";");
        s.handleCommand("CREATE TABLE marks (name, mark, pass);");
        s.handleCommand("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        // Create a new server object
        DBServer server = new DBServer();
        server.handleCommand("USE " + randomName + ";");
        String response = server.handleCommand("SELECT * FROM marks;");
        assertTrue(response.contains("Simon"), "Simon was added to a table and the server restarted - but Simon was not returned by SELECT *");
        server.handleCommand("DROP DATABASE " + randomName + ";");

    }

    @Test
    public void testUsingNonExistentDB() {
        DBServer s = new DBServer();
        assertThrows(NotFound.class, () -> new Parser("USE test;", s));
    }

    @Test
    public void testBasicTableCommands() throws GenericException {
        DBServer s = new DBServer();
        new Parser("CREATE DATABASE test;", s);
        new Parser("USE test;", s);
        new Parser("CREATE TABLE t;", s);
        new Parser("DROP TABLE t;", s);
        new Parser("CREATE TABLE marks (name, mark, pass);", s);
        assertNotNull(s.getCurrentDatabase());
        assertNotNull(s.getCurrentDatabase().getTableByName("marks"));
        String tableToString = s.getCurrentDatabase().getTableByName("marks").toString();
        assertTrue(tableToString.contains("name\tmark\tpass"));
        new Parser("INSERT INTO marks VALUES ('Simon', 65, TRUE);", s);
        new Parser("INSERT INTO marks VALUES ('David', 12, FALSE);", s);
        new Parser("INSERT INTO marks VALUES ('Tony', 123, TRUE);", s);
        assertTrue(s.getCurrentDatabase().getTableByName("marks").toString().contains(
                "id\tname\tmark\tpass\n" +
                        "1\tSimon\t65\tTRUE\n" +
                        "2\tDavid\t12\tFALSE\n" +
                        "3\tTony\t123\tTRUE\n"));
        new Parser("DROP DATABASE test;", s);
    }

    @Test
    public void selectTest() {
        DBServer s = new DBServer();
        s.handleCommand("CREATE DATABASE d;");
        s.handleCommand("USE d;");
        s.handleCommand("CREATE table h (col1, col2, col3);");
        s.handleCommand("INSERT INTO h VALUES ('test', FALSE, 2);");
        String ret = s.handleCommand("SELECT * FROM h;");
        assertTrue(ret.contains(
                """
                        [OK]
                        id\tcol1\tcol2\tcol3
                        1\ttest\tFALSE\t2
                        """), "Expected OK and query results. Not got exact match..");
        String ret2 = s.handleCommand("SELECT * FROM hoo;");
        assertTrue(ret2.contains("[ERROR]: Table hoo not found."), "Got " + ret2 + " Instead of expected string.");
        s.handleCommand("DROP DATABASE d;");
    }

    //


}
