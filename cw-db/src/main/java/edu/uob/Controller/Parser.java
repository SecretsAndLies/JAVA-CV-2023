package edu.uob.Controller;

import edu.uob.Controller.Command.CreateCommand;
import edu.uob.Controller.Command.DropCommand;
import edu.uob.Controller.Command.UseCommand;
import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Command.SemiColonNotFound;

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
        int last_element_index = this.tokens.size()-1;
        String last_element = this.tokens.get(last_element_index);
        if(!last_element.equals(";")){
            throw new SemiColonNotFound(query);
        }
        if(this.tokens.get(0).equals("CREATE")){
            parseCreateCommand();
        }
        if(this.tokens.get(0).equals("DROP")){
            // DROP TABLE t;
            // DROP DATABASE d;
            if(this.tokens.size()!=4) throw new InvalidCommand();
            this.returnString = new DropCommand(server,this.tokens.get(1),this.tokens.get(2)).getReturnString();
        }
        if(this.tokens.get(0).equals("USE")){
            // USE markbook;
            if(this.tokens.size()!=3) throw new InvalidCommand();
            this.returnString = new UseCommand(server,this.tokens.get(1)).getReturnString();
        }
        if(this.returnString.isEmpty()){
            this.returnString = "[OK]";
        }
    }

    public String getReturnString() {
        return returnString;
    }

    private void parseCreateCommand() throws GenericException {
        // CREATE TABLE t;
        // CREATE DATABASE markbook;
        if(this.tokens.size()==4){
            this.returnString = new CreateCommand(server,this.tokens.get(1),this.tokens.get(2)).getReturnString();
        }
        else {
            // CREATE TABLE marks (name, mark, pass);
            if(!this.tokens.get(1).equals("TABLE")){
                throw new InvalidCommand();
            }
            currentTokenIndex=3;
            this.returnString = new CreateCommand(server,this.tokens.get(2),parseAttributeList()).getReturnString();
            if(!tokens.get(currentTokenIndex).equals(";")){
                // todo test the case of CREATE TABLE marks (name, mark, pass) asdasdads;
                throw new SemiColonNotFound(query);
            }
        }
    }

    public ArrayList<String> parseAttributeList() throws InvalidCommand {
        ArrayList<String> attributes = new ArrayList<>();
        if(!tokens.get(currentTokenIndex).equals("(")){
            // todo: test this throws this command if you don't start attribute list with a (
            throw new InvalidCommand();
        }
        // go past (
        currentTokenIndex++;
        while (!tokens.get(currentTokenIndex).equals(")")){
            if(currentTokenIndex==tokens.size()-1){
                // todo: test this throws an error when you have queries like create table t ( asdas ;
                throw new InvalidCommand();
            }
            // todo: this would validate weird stuff like "(,,,test,)
            if(tokens.get(currentTokenIndex).equals(",")){
                currentTokenIndex++;
                continue;
            }
            attributes.add(tokens.get(currentTokenIndex));
            currentTokenIndex++;
        }
        // go past the final ")"
        currentTokenIndex++;
        return attributes;
    }

}

