package edu.uob;

import edu.uob.Controller.Tokenizer;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TokenizerTests {

    @Test
    public void testWhiteSpace() {
        Tokenizer tokenizer = new Tokenizer(
                "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ;   ");
        List<String> tokens = tokenizer.getTokens();
        assertEquals("INSERT", tokens.get(0));
        assertEquals(";", tokens.get(13));
        assertEquals(14, tokens.size());
    }


    @Test
    public void testCreateQuery() {
        Tokenizer tokenizer = new Tokenizer(
                "  CREATE DATABASE   markbook   ;   ");
        List<String> tokens = tokenizer.getTokens();
        assertEquals("CREATE", tokens.get(0));
        assertEquals(";", tokens.get(3));
        assertEquals(4, tokens.size());
    }

    @Test
    public void testSelectQuery(){
        Tokenizer tokenizer = new Tokenizer(
                " SELECT * FROM    marks    WHERE pass == TRUE;  ");
        List<String> tokens = tokenizer.getTokens();
        assertEquals("SELECT", tokens.get(0));
        assertEquals("*", tokens.get(1));
        assertEquals("==", tokens.get(6));
        assertEquals("TRUE", tokens.get(7));
        assertEquals(";", tokens.get(8));
        assertEquals(9, tokens.size());
    }

    @Test
    public void testCaseQuery(){
        Tokenizer tokenizer = new Tokenizer(
                " sElecT * frOM    marks     WHERe pass == true;  ");
        List<String> tokens = tokenizer.getTokens();
        assertEquals("SELECT", tokens.get(0));
        assertEquals("*", tokens.get(1));
        assertEquals("==", tokens.get(6));
        assertEquals("TRUE", tokens.get(7));
        assertEquals(";", tokens.get(8));
        assertEquals(9, tokens.size());

    }

    @Test
    public void testConditionalQuery(){
        Tokenizer tokenizer = new Tokenizer(
                " DELETE FROM marks WHERE mark<40; ");
        List<String> tokens = tokenizer.getTokens();
        assertEquals("DELETE", tokens.get(0));
        assertEquals("FROM", tokens.get(1));
        assertEquals("marks", tokens.get(2));
        assertEquals("WHERE", tokens.get(3));
        assertEquals("mark", tokens.get(4));
        assertEquals("<", tokens.get(5));
        assertEquals("40", tokens.get(6));
        assertEquals(";", tokens.get(7));
        assertEquals(8, tokens.size());
    }

    @Test
    public void testEqualityQuery(){
        Tokenizer tokenizer = new Tokenizer(
                " UPDATE marks SET age = 35 WHERE name == 'Simon'" +
                        " AND age < 35 AND age>35 AND age>=35 AND age <=35" +
                        " AND name != 'test'; ");
        List<String> tokens = tokenizer.getTokens();
        assertEquals("UPDATE", tokens.get(0));
        assertEquals("marks", tokens.get(1));
        assertEquals("SET", tokens.get(2));
        assertEquals("age", tokens.get(3));
        assertEquals("=", tokens.get(4));
        assertEquals("35", tokens.get(5));
        assertEquals("WHERE", tokens.get(6));
        assertEquals("name", tokens.get(7));
        assertEquals("==", tokens.get(8));
        assertEquals("'Simon'", tokens.get(9));
        assertEquals("AND", tokens.get(10));
        assertEquals("age", tokens.get(11));
        assertEquals("<", tokens.get(12));
        assertEquals("35", tokens.get(13));
        assertEquals("AND", tokens.get(14));
        assertEquals("age", tokens.get(15));
        assertEquals(">", tokens.get(16));
        assertEquals("35", tokens.get(17));
        assertEquals("AND", tokens.get(18));
        assertEquals("age", tokens.get(19));
        assertEquals(">=", tokens.get(20));
        assertEquals("35", tokens.get(21));
        assertEquals("AND", tokens.get(22));
        assertEquals("age", tokens.get(23));
        assertEquals("<=", tokens.get(24));
        assertEquals("35", tokens.get(25));
        assertEquals("AND", tokens.get(26));
        assertEquals("name", tokens.get(27));
        assertEquals("!=", tokens.get(28));
        assertEquals("'test'", tokens.get(29));
        assertEquals(";", tokens.get(30));
        assertEquals(31, tokens.size());
    }

}
