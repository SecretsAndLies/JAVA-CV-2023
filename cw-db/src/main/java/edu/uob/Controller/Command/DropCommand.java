package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.ReservedKeyword;
import edu.uob.Exceptions.GenericException;
import edu.uob.Model.Database;
import edu.uob.Model.Table;

import java.util.ArrayList;

import static edu.uob.Utils.Utils.isReservedKeyword;

public class DropCommand extends Command{
    public DropCommand(DBServer server, String location, String name) throws GenericException {
        super(server);
        if(isReservedKeyword(name)){
            throw new ReservedKeyword(name); // todo this could be a table also.
        }
        if(location.equals("DATABASE")) {
            this.databaseName=name;
            Database database = new Database(databaseName);
            database.dropDatabase();
            if(server.getCurrentDatabase().getName().equals(databaseName)){
                server.setCurrentDatabase(null);
            }
        }
        else if(location.equals("TABLE")){
            // todo: why do you need to store these things in the overall command structure?
            Table t = new Table(name, server.getCurrentDatabase());
            t.delete();
        }
        else{
            // todo: test thatkeyword isn't DATABASE or TABLE throws this error.
            throw new InvalidCommand();
        }

    }
    // todo this is intended as the constructor for the table...
    public DropCommand(DBServer server, String location, String name, ArrayList<String> columns){
        super(server);
    }
}
