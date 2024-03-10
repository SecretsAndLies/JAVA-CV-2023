package edu.uob.Controller;

import edu.uob.DBServer;
import edu.uob.Exceptions.Database.ReservedKeyword;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Command.SemiColonNotFound;
import edu.uob.Model.Database;

import java.util.List;

import static edu.uob.Utils.Utils.isReservedKeyword;

public class CommandHandler {
    private final List<String> tokens;
    private String returnString;
    private final DBServer server;

    public CommandHandler(String query, DBServer server) throws GenericException {
        this.tokens = new Tokenizer(query).getTokens();
        this.returnString = "";
        this.server = server;

        // TODO: implement a fuller grammar check at this point before the query is interpreted?
        int last_element_index = this.tokens.size()-1;
        String last_element = this.tokens.get(last_element_index);
        if(!last_element.equals(";")){
            throw new SemiColonNotFound(query);
        }
        // todo - you'll need to think about how this handles tables vs databases, which use the same syntax.
        if(this.tokens.get(0).equals("CREATE") && this.tokens.get(1).equals("DATABASE")){
            handleCreateOrDrop();
        }
        if(this.tokens.get(0).equals("DROP") && this.tokens.get(1).equals("DATABASE")){
            handleCreateOrDrop();
        }
        if(this.tokens.get(0).equals("USE")){
            handleUse();
        }
        // todo: this might not be the best way to handle this?
        if(this.returnString.isEmpty()){
            this.returnString = "[OK]";
        }
    }

    public String getReturnString() {
        return returnString;
    }

    private void handleUse(){
        // todo: all of these commands have an expected number of tokens... Can you check in a generic way?
        String databaseName  = this.tokens.get(1);
        server.setCurrentDatabase(new Database(databaseName));
    }

    private void handleCreateOrDrop() throws GenericException {
        String databaseName  = this.tokens.get(2);
        if(isReservedKeyword(databaseName)){
            throw new ReservedKeyword(databaseName);
        }
        Database database = new Database(databaseName);
        String command = this.tokens.get(0);
        if (command.equals("DROP")){
            database.dropDatabase();
            server.setCurrentDatabase(null);
        }
        if (command.equals("CREATE")){
            database.createDatabase();
        }
    }

}

