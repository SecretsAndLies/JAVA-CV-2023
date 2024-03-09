package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

public class TokenizerTests {

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)

    @Test
    public void testWhiteSpace() {
        Tokenizer tokenizer = new Tokenizer();
        String[] cleanQuery = tokenizer.tokenise(
                "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ;   ");
        // todo make this a test with asserts.
        System.out.println(Arrays.stream(cleanQuery).toList());
    }

}
