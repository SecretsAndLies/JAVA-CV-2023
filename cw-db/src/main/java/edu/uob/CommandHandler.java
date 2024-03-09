package edu.uob;

import java.util.ArrayList;

public class QueryHandler {
    ArrayList<String> tokens;
    public QueryHandler(String query) {
        this.tokens = new Tokenizer(query).getTokens();
    }
}


// gets tokenized then // parse the incoming command,
// perform the specified query, update the data stored in the database
// and return an appropriate result to the client.