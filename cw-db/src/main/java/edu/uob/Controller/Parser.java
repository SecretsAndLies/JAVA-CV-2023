package edu.uob.Controller;

import edu.uob.Controller.Command.*;
import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Command.SemiColonNotFound;
import edu.uob.Exceptions.Table.ColNotFound;
import edu.uob.Exceptions.Table.InsertionError;
import edu.uob.Exceptions.Table.InvalidName;
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
        switch (this.tokens.get(0)) {
            case "CREATE":
                parseCreateCommand();
                break;
            case "DROP":
                if (this.tokens.size() != 4) throw new InvalidCommand();
                this.returnString = new DropCommand(server, this.tokens.get(1), this.tokens.get(2)).getReturnString();
                break;
            case "USE":
                if (this.tokens.size() != 3) throw new InvalidCommand();
                this.returnString = new UseCommand(server, this.tokens.get(1)).getReturnString();
                break;
            case "INSERT":
                parseInsertCommand();
                break;
            case "SELECT":
                parseSelectCommand();
                break;
            case "ALTER":
                parseAlterCommand();
                break;
            case "DELETE":
                parseDeleteCommmand();
                break;
            default:
                // Handle the case where none of the above commands match.
                // Only set to "[OK]" if it's still empty, to avoid overwriting meaningful return strings.
                if (this.returnString.isEmpty()) {
                    this.returnString = "[OK]";
                }
                break;
        }

    }

    private void parseDeleteCommmand() throws GenericException {
        // DELETE from marks WHERE age > 36;
        if (!this.tokens.get(1).equals("FROM")) {
            throw new InvalidCommand("Expected FROM");
        }
        if (!this.tokens.get(3).equals("WHERE")) {
            throw new InvalidCommand("Expected WHERE");
        }
        currentTokenIndex = 3;
        ArrayList<String> conditions = parseWhereStatement();
        isQueryEnd();
        this.returnString = new DeleteCommand(server, tokens.get(2), tokens.get(4), conditions).getReturnString();

    }

    private void parseAlterCommand() throws InvalidCommand, InvalidName, InternalError, ColNotFound {
        //  "ALTER " "TABLE " [TableName] " " <AlterationType> " " [AttributeName]
        if (!this.tokens.get(1).equals("TABLE")) {
            throw new InvalidCommand("Expected TABLE");
        }
        this.returnString = new AlterCommand(server, tokens.get(2), tokens.get(3), tokens.get(4)).getReturnString();
        currentTokenIndex = 4;
        isQueryEnd();

    }

    private void parseSelectCommand() throws GenericException {
        currentTokenIndex = 1;
        ArrayList<String> colList = parseAttributeList("FROM");
        if (colList.get(0).equals("*")) {
            if (colList.size() != 1) {
                throw new InvalidCommand("* cannot be used with other cols.");
            }
            currentTokenIndex = 4;
            ArrayList<String> conditions = parseWhereStatement();
            if (conditions.isEmpty()) {
                this.returnString = new SelectCommand(server, tokens.get(3)).getReturnString();
            }
            this.returnString = new SelectCommand(server, tokens.get(3), colList, conditions).getReturnString();
        } else {
            int tableNameIndex = currentTokenIndex;
            currentTokenIndex++; // go past the table name.
            ArrayList<String> conditions = parseWhereStatement();
            this.returnString = new SelectCommand(server, tokens.get(tableNameIndex), colList, conditions).getReturnString();
        }
        isQueryEnd();
    }

    private ArrayList<String> parseWhereStatement() throws InvalidCommand {
        ArrayList<String> condtions = new ArrayList<>();
        if (tokens.get(currentTokenIndex).equals(";")) {
            return condtions;
        }
        if (!tokens.get(currentTokenIndex).equals("WHERE")) {
            throw new InvalidCommand("Expected WHERE or end of query.");
        }
        currentTokenIndex++; // go past where.
        while (!tokens.get(currentTokenIndex).equals(";")) {
            condtions.add(tokens.get(currentTokenIndex));
            currentTokenIndex++;
        }
        return condtions;
    }

    public String getReturnString() {
        return returnString;
    }

    private void parseInsertCommand() throws GenericException {
        //INSERT INTO marks VALUES ('Simon', 65, TRUE);
        // "INSERT " "INTO " [TableName] " VALUES" "(" <ValueList> ")"
        if (!tokens.get(1).equals("INTO")) {
            throw new InvalidCommand("Expected 'INTO'");
        }
        if (!tokens.get(3).equals("VALUES")) {
            throw new InvalidCommand("Expected 'VALUES'.");
        }
        currentTokenIndex = 4;
        if (!tokens.get(currentTokenIndex).equals("(")) {
            throw new InvalidCommand();
        }
        // go past (
        currentTokenIndex++;
        String tableName = tokens.get(2);
        this.returnString = new InsertCommand(server, parseAttributeList(")"), tableName).getReturnString();
        isQueryEnd();
    }

    // expects currentToken to be set at where the semi colon should be
    private void isQueryEnd() throws InvalidCommand {
        if (!tokens.get(currentTokenIndex).equals(";")) {
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
                throw new InvalidCommand();
            }
            // go past (
            currentTokenIndex++;
            this.returnString = new CreateCommand(server, this.tokens.get(2), parseAttributeList(")")).getReturnString();
            if (!tokens.get(currentTokenIndex).equals(";")) {
                throw new SemiColonNotFound(query);
            }
        }
    }

    // This method expects you to be on the first attribute in the list.
    public ArrayList<String> parseAttributeList(String stopAt) throws InvalidCommand {
        ArrayList<String> attributes = new ArrayList<>();
        while (!tokens.get(currentTokenIndex).equals(stopAt)) {
            if (currentTokenIndex == tokens.size() - 1) {
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

