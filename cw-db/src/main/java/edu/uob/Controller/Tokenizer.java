package edu.uob.Controller;

import edu.uob.Utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokenizer {
    private final String[] specialCharacters;
    private final List<String> tokens;
    private String query;

    public Tokenizer(String query) {
        this.query = query;
        this.specialCharacters = new String[]{"(", ")", ",", ";","!" , ">" , "<" , "="};
        this.tokens = new ArrayList<>();
        setup();
        handleReservedKeywords();
    }
    public List<String> getTokens() {
        return tokens;
    }

    private void handleReservedKeywords(){
        for (int i=0; i<tokens.size(); i++){
            if(Utils.isReservedKeyword(tokens.get(i))){
                tokens.set(i,tokens.get(i).toUpperCase());
            }
        }
    }

    private void setup() {
        // Remove any whitespace at the beginning and end of the query
        query = query.trim();
        // Split the query on single quotes (to separate out query characters from string literals)
        String[] fragments = query.split("'");
        for (int i = 0; i < fragments.length; i++) {
            // Every odd fragment is a string literal, so just append it without any alterations
            if (i % 2 != 0) tokens.add("'" + fragments[i] + "'");
                // If it's not a string literal, it must be query characters (which need further processing)
            else {
                // Tokenize the fragments into an array of strings
                String[] nextBatchOfTokens = tokenize(fragments[i]);
                // Then add these to the "result" array list (needs a bit of conversion)
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
    }

    private String[] tokenize(String input) {
        // Add in some extra padding spaces around the "special characters"
        // so we can be sure that they are separated by AT LEAST one space (possibly more)
        for (String specialCharacter : specialCharacters) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        // Remove all double spaces (the previous replacements may have added some)
        // This is "blind" replacement - replacing if they exist, doing nothing if they don't
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        // Again, remove any whitespace from the beginning and end that might have been introduced
        input = input.trim();

        // combine adjacent = =, ! =, < =, and > = to be together with no spaces.
        String[] specialStrings = new String[]{"= =", "! =", "< =", "> ="};
        for (String specialString : specialStrings) {
            if (input.contains(specialString)) {
                input = input.replace(specialString, specialString.replace(" ", ""));
            }
        }

        // Finally split on the space char (since there will now ALWAYS be a space between tokens)
        return input.split(" ");
    }
}
