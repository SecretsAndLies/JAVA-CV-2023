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

    // test other  queries work
    //

}
