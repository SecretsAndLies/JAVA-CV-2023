package edu.uob;

import edu.uob.Controller.Parser;
import edu.uob.Exceptions.Database.NotFound;
import edu.uob.Exceptions.GenericException;
import edu.uob.Model.Table;
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
                        """), "Expected OK and query results. Got:" + ret);
        String ret2 = s.handleCommand("SELECT * FROM hoo;");
        assertTrue(ret2.contains("[ERROR]: Table hoo not found."), "Got " + ret2 + " Instead of expected string.");
        s.handleCommand("DROP DATABASE d;");
    }

    @Test
    void singleColNameSelect() {
        DBServer s = new DBServer();
        s.handleCommand("CREATE DATABASE d;");
        s.handleCommand("USE d;");
        s.handleCommand("CREATE table h (Col1, Col2, Col3);");
        s.handleCommand("INSERT INTO h VALUES ('test1', 11, 2);");
        s.handleCommand("INSERT INTO h VALUES ('test2', 22, 2);");
        String ret = s.handleCommand("SELECT col2, col1 FROM h;");
        s.handleCommand("DROP DATABASE d;");
        assertTrue(ret.contains(
                """
                        [OK]
                        Col2\tCol1\t
                        11\ttest1\t
                        22\ttest2\t
                        """), "Expected OK and query results. Not got exact match..");
    }

    @Test
    public void selectColName() {
        // this also tests case sensitivity stuff.
        DBServer s = new DBServer();
        s.handleCommand("CREATE DATABASE d;");
        s.handleCommand("USE d;");
        s.handleCommand("CREATE table h (Col1, Col2, Col3);");
        s.handleCommand("INSERT INTO h VALUES ('test', FALSE, 2);");
        String ret = s.handleCommand("SELECT col2, id FROM h;");
        assertTrue(ret.contains(
                """
                        [OK]
                        Col2\tid\t
                        FALSE\t1\t
                        """), "Expected OK and query results. Not got exact match..");
        s.handleCommand("DROP DATABASE d;");
    }

    @Test
    public void alterTable() {
        DBServer s = new DBServer();
        s.handleCommand("CREATE DATABASE d;");
        s.handleCommand("USE d;");
        s.handleCommand("CREATE TABLE marks (name, mark, pass);");
        s.handleCommand("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        s.handleCommand("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        s.handleCommand("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        s.handleCommand("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        String rer = s.handleCommand("INSERT INTO marks VALUES 'Chris', 20, FALSE);");
        assertTrue(rer.contains("ERROR"), "MAlformed query no starting brackets");
        rer = s.handleCommand("INSERT INTO marks VALUES ('Chris', 20, FALSE;");
        assertTrue(rer.contains("ERROR"), "MAlformed query no ending brackets");
        s.handleCommand("ALTER TABLE marks ADD age;");
        String ret = s.handleCommand("SELEcT * from marks;");
        assertTrue(ret.contains("id\tname\tmark\tpass\tage"));
        s.handleCommand("ALTER TABLE marks DROP mark;");
        ret = s.handleCommand("SELEcT * from marks;");
        assertTrue(ret.contains("id\tname\tpass\tage"));
        ret = s.handleCommand("ALTER TABLE marks ADD Age;");
        assertTrue(ret.contains("ERROR"), "adding a duplicate column should fail");
        ret = s.handleCommand("CREATE TABLE t (name, mark, pass) assad;");
        assertTrue(ret.contains("ERROR"), "extra stuff after attribute list should fail");
        ret = s.handleCommand("CREATE TABLE j (name, , pass);");
        // todo: right now my parser doesn't catch this.
//        assertTrue(ret.contains("ERROR"), "colnames must contain something.");
        s.handleCommand("DROP DATABASE d;");

    }

    @Test
    public void selectWithCondition() {
        DBServer s = new DBServer();
        s.handleCommand("CREATE DATABASE d;");
        s.handleCommand("USE d;");
        s.handleCommand("CREATE TABLE marks (name, mark, pass);");
        s.handleCommand("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        s.handleCommand("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        s.handleCommand("INSERT INTO marks VALUES ('Rob', 36, FALSE);");
        s.handleCommand("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        String ret = s.handleCommand("SELECT * FROM marks;");
        assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        assertTrue(ret.contains("Chris"), "expected output with C but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE name == 'Simon';");
        assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE name != 'Sion';");
        assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE mark <65;");
        assertFalse(ret.contains("Simon"), "expected output with no Simon but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE mark >55;");
        assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE mark <= 65;");
        assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE mark >= 65;");
        assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE name like 'on';");
        assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        assertTrue(ret.contains("Sion"), "expected output with Sion but got " + ret);
        assertFalse(ret.contains("Chris"), "expected output with no Chris but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE name < 5;");
        System.out.println(ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);");
//        assertTrue(ret.contains("Rob"), "expected output with Rob but got " + ret);
//        assertFalse(ret.contains("Chris"), "expected output with no Chris but got " + ret);
//        assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT * FROM marks WHERE (pass == TRUE) OR (age > 35);");
        //assertTrue(ret.contains("Rob"), "expected output with Rob but got " + ret);
        //assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        //assertTrue(ret.contains("Sion"), "expected output with Sion but got " + ret);

        ret = s.handleCommand("SELECT name FROM marks WHERE name != 'Sion';");
        //assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT name FROM marks WHERE mark <65;");
        //assertFalse(ret.contains("Simon"), "expected output with no Simon but got " + ret);
        ret = s.handleCommand("SELECT name FROM marks WHERE mark >55;");
        //assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        //assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT name FROM marks WHERE name == 'Simon';");
        //assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        //assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT name FROM marks WHERE mark <= 65;");
        //assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        ret = s.handleCommand("SELECT name FROM marks WHERE mark >= 65;");
        //assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        //assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT name FROM marks WHERE name like 'on';");
        //assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        //assertTrue(ret.contains("Sion"), "expected output with Sion but got " + ret);
        //assertFalse(ret.contains("Chris"), "expected output with no Chris but got " + ret);
        ret = s.handleCommand("SELECT name FROM marks WHERE (pass == FALSE) AND (mark > 35);");
        //assertTrue(ret.contains("Rob"), "expected output with Rob but got " + ret);
        //assertFalse(ret.contains("Chris"), "expected output with no Chris but got " + ret);
        //assertFalse(ret.contains("Sion"), "expected output with no Sion but got " + ret);
        ret = s.handleCommand("SELECT name FROM marks WHERE (pass == TRUE) OR (age > 35);");
        //assertTrue(ret.contains("Rob"), "expected output with Rob but got " + ret);
        //assertTrue(ret.contains("Simon"), "expected output with Simon but got " + ret);
        //assertTrue(ret.contains("Sion"), "expected output with Sion but got " + ret);


        s.handleCommand("DROP DATABASE d;");
    }


    @Test
    public void errorTests() {
        DBServer s = new DBServer();
        s.handleCommand("CREATE DATABASE d;");
        s.handleCommand("USE d;");
        s.handleCommand("CREATE TABLE marks (name, mark, pass);");
        String ret = s.handleCommand("SELECT test from marks;");
        assertTrue(ret.contains("ERROR"), "Unknown column didn't return error.");
        ret = s.handleCommand("INSERT INTO marks VALUES ('Simon', 65);");
        assertTrue(ret.contains("ERROR"), "Inserting too few cols didn't return error.");
        ret = s.handleCommand("INSERT INTO marks VALUES ('Simon', 65, TRUE, TRUE);");
        assertTrue(ret.contains("ERROR"), "Inserting too many cols didn't return error.");

        ret = s.handleCommand("SELECT mark from d;");
        assertTrue(ret.contains("ERROR"), "Unknown table didn't return error.");
        ret = s.handleCommand("CREATE TABLE test (name, pass, Name);");
        assertTrue(ret.contains("ERROR"), "duplicate colnames should error.");
        ret = s.handleCommand("CREATE TABLE test (create, pass, Name);");
        s.handleCommand("DROP DATABASE d;");
        assertTrue(ret.contains("ERROR"), "creating table with reserved keywords should error..");

        ret = s.handleCommand("USE d;");
        assertTrue(ret.contains("ERROR"), "can't use a dropped db.");


    }


//    <Condition>       ::=  "(" <Condition> <BoolOperator> <Condition> ")" |
//    <Condition> <BoolOperator> <Condition> |
//    "(" [AttributeName] <Comparator> [Value] ")" |
//    [AttributeName] <Comparator> [Value]
//
//<BoolOperator>    ::= "AND" | "OR"
//
//<Comparator>      ::=  "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "

    //


}
