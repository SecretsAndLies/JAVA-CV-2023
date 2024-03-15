package edu.uob.Controller;

import edu.uob.Controller.Command.*;
import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Command.SemiColonNotFound;
import edu.uob.Exceptions.Table.InsertionError;
import edu.uob.Exceptions.Table.NotFound;

import java.util.ArrayList;
import java.util.List;


public class Parser {
    private final List<String> tokens;
    private String returnString;
    private final DBServer server;
    private int currentTokenIndex;
    private String query;

    public Parser(String query, DBServer server) throws GenericException {
        this.tokens = new Tokenizer(query).getTokens();
        this.returnString = "";
        this.server = server;
        parseCommand(query);

    }

    private void parseCommand(String query) throws GenericException {
        this.query = query;
        int last_element_index = this.tokens.size() - 1;
        String last_element = this.tokens.get(last_element_index);
        if (!last_element.equals(";")) {
            throw new SemiColonNotFound(query);
        }
        if (this.tokens.get(0).equals("CREATE")) {
            parseCreateCommand();
        }
        if (this.tokens.get(0).equals("DROP")) {
            // DROP TABLE t;
            // DROP DATABASE d;
            if (this.tokens.size() != 4) throw new InvalidCommand();
            this.returnString = new DropCommand(server, this.tokens.get(1), this.tokens.get(2)).getReturnString();
        }
        if (this.tokens.get(0).equals("USE")) {
            // USE markbook;
            if (this.tokens.size() != 3) throw new InvalidCommand();
            this.returnString = new UseCommand(server, this.tokens.get(1)).getReturnString();
        }
        if (this.tokens.get(0).equals("INSERT")) {
            parseInsertCommand();
        }
        if (this.tokens.get(0).equals("SELECT")) {
            parseSelectCommand();
        }
        if (this.returnString.isEmpty()) {
            this.returnString = "[OK]";
        }
    }

    private void parseSelectCommand() throws NotFound {
        // select * from table;

        //<"SELECT " <WildAttribList> " FROM " [TableName] | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
        // todo: this is bad. Do this the proper recursive way so you catch the edge cases like select blah from table;
        // use your new fancy attribute list thing.
        if (tokens.size() == 5) {
            this.returnString = new SelectCommand(server, tokens.get(3)).getReturnString();
        }
    }

    public String getReturnString() {
        return returnString;
    }

    private void parseInsertCommand() throws GenericException {
        //INSERT INTO marks VALUES ('Simon', 65, TRUE);
        // "INSERT " "INTO " [TableName] " VALUES" "(" <ValueList> ")"
        // todo: could improve this by having a "word missing" error, and then pass the missing word in?
        if (!tokens.get(1).equals("INTO")) {
            throw new InvalidCommand("Expected 'INTO'");
        }
        if (!tokens.get(3).equals("VALUES")) {
            throw new InvalidCommand("Expected 'VALUES'.");
        }
        currentTokenIndex = 4;
        if (!tokens.get(currentTokenIndex).equals("(")) {
            // todo: test this throws this command if you don't start attribute list with a (
            throw new InvalidCommand();
        }
        String tableName = tokens.get(2);
        this.returnString = new InsertCommand(server, parseAttributeList(")"), tableName).getReturnString();
        isQueryEnd();
    }

    // expects currentToken to be set at where the semi colon should be
    private void isQueryEnd() throws InvalidCommand {
        if (!tokens.get(currentTokenIndex).equals(";")) {
            // todo: should be a semi colon error.
            throw new InvalidCommand("Semicolon not found in expected location.");
        }
    }

    private void parseCreateCommand() throws GenericException {
        // CREATE TABLE t;
        // CREATE DATABASE markbook;
        if (this.tokens.size() == 4) {
            this.returnString = new CreateCommand(server, this.tokens.get(1), this.tokens.get(2)).getReturnString();
        } else {
            // CREATE TABLE marks (name, mark, pass);
            if (!this.tokens.get(1).equals("TABLE")) {
                throw new InvalidCommand();
            }
            currentTokenIndex = 3;
            if (!tokens.get(currentTokenIndex).equals("(")) {
                // todo: test this throws this command if you don't start attribute list with a (
                throw new InvalidCommand();
            }
            this.returnString = new CreateCommand(server, this.tokens.get(2), parseAttributeList(")")).getReturnString();
            if (!tokens.get(currentTokenIndex).equals(";")) {
                // todo test the case of CREATE TABLE marks (name, mark, pass) asdasdads;
                throw new SemiColonNotFound(query);
            }
        }
    }

    // This method expects you to be on the first attribute in the list.
    public ArrayList<String> parseAttributeList(String stopAt) throws InvalidCommand {
        ArrayList<String> attributes = new ArrayList<>();
        // go past (
        currentTokenIndex++;
        while (!tokens.get(currentTokenIndex).equals(stopAt)) {
            if (currentTokenIndex == tokens.size() - 1) {
                // todo: test this throws an error when you have queries like create table t ( asdas ;
                throw new InvalidCommand();
            }
            // todo: this would validate weird stuff like "(,,,test,)
            if (tokens.get(currentTokenIndex).equals(",")) {
                currentTokenIndex++;
                continue;
            }
            attributes.add(tokens.get(currentTokenIndex));
            currentTokenIndex++;
        }
        // go past the final ends with token.
        currentTokenIndex++;
        return attributes;
    }

}

