package edu.uob;

import edu.uob.Exceptions.DatabaseNotFound;
import edu.uob.Model.Database;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    private final ArrayList<String> tokens;
    private String returnString;

    public CommandHandler(String query) throws DatabaseNotFound {
        this.tokens = new Tokenizer(query).getTokens();
        this.returnString = "";

        // TODO: implement a fuller grammar check at this point before the query is interpreted?
        int last_element_index = this.tokens.size()-1;
        String last_element = this.tokens.get(last_element_index);
        if(!last_element.equals(";")){
        // todo: consider if the errors should be like this (vs throwing an exception that you check in some later class...)
            this.returnString =
                    "[ERROR]: error: you must end all commands with a semi colon.";
            return;
        }
        if(this.tokens.get(0).equals("CREATE") && this.tokens.get(1).equals("DATABASE")){
            handleCreateDatabaseCommand();
        }
        if(this.tokens.get(0).equals("DROP") && this.tokens.get(1).equals("DATABASE")){
            handleDropDatabaseCommand();
        }
        if(this.returnString.isEmpty()){
            this.returnString = "[OK]";
        }

    }

    public String getReturnString() {
        return returnString;
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
        // todo: in the future this will raise an error that I catch somehow.
        if(this.isReservedKeyword(databaseName)){
            this.returnString =
                    "[ERROR]: You cannot call a database "+ databaseName +
                            " because it is a reserved keyword in SQL.";
            return;
        }
        Database database = new Database(databaseName);
        database.createDatabase();
    }

    private void handleDropDatabaseCommand() throws DatabaseNotFound {
        // todo: this is pretty duplicative of create
        String databaseName  = this.tokens.get(2);
        if(this.isReservedKeyword(databaseName)){
            this.returnString =
                    "[ERROR]: You cannot call a database "+ databaseName +
                            " because it is a reserved keyword in SQL.";
            return;
        }
        Database database = new Database(databaseName);
        database.dropDatabase();
    }
}

