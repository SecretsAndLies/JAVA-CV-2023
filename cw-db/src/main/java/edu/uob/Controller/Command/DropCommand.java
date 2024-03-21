package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.ReservedKeyword;
import edu.uob.Exceptions.GenericException;
import edu.uob.Model.Database;
import edu.uob.Model.Table;

import java.util.ArrayList;

import static edu.uob.Utils.Utils.isReservedKeyword;

public class DropCommand extends Command {
    public DropCommand(DBServer server, String location, String name) throws GenericException {
        super(server);
        if (isReservedKeyword(name)) {
            throw new ReservedKeyword(name); // todo this could be a table also.
        }
        if (location.equals("DATABASE")) {
            this.databaseName = name;
            Database database = new Database(databaseName);
            database.dropDatabase();
            if (server.getCurrentDatabase().getName().equals(databaseName)) {
                server.setCurrentDatabase(null);
            }
        } else if (location.equals("TABLE")) {
            server.getCurrentDatabase().deleteTable(name);
        } else {
            throw new InvalidCommand();
        }

    }

}
