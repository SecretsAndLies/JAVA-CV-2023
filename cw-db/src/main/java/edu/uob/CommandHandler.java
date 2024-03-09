package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    ArrayList<String> tokens;

    public CommandHandler(String query) {
        this.tokens = new Tokenizer(query).getTokens();
        if(this.tokens.get(0).equals("CREATE") && this.tokens.get(1).equals("DATABASE")){
            handleCreateDatabaseCommand();
        }
        if(this.tokens.get(0).equals("DROP") && this.tokens.get(1).equals("DATABASE")){
            handleDropDatabaseommand();
        }

    }

    // checks that a word doesn't contain reserved words
    // TODO: duplicative of something in the Tokenizer class. Can you combine?
    private boolean isReservedKeyword(String name){
        List<String> reservedKeywords = new ArrayList<>(
                List.of("USE", "CREATE", "DATABASE", "TABLE", "DROP",
                        "ALTER", "INSERT", "INTO", "VALUES", "SELECT", "FROM",
                        "WHERE", "UPDATE", "SET", "DELETE", "JOIN", "ON", "ADD",
                        "TRUE", "FALSE", "NULL", "AND", "OR", "LIKE"
                ));
        return reservedKeywords.contains(name);
    }

    private void handleCreateDatabaseCommand() {
        // eg: CREATE DATABASE t;
        String databaseName  = this.tokens.get(2);
        if(this.isReservedKeyword(databaseName)){
            // TODO: figure out the error that this returns, and how to bubble it up.
        }
    Database database = new Database(databaseName);
    }

    private void handleDropDatabaseommand() {
        // eg: DROP DATABASE t;

        // checks if that folder exists. If not, it returns an error.
        // deletes that folder.
    }
}

