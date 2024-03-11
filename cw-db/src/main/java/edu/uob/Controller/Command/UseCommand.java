package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Model.Database;

public class UseCommand extends Command {
    public UseCommand(DBServer server, String databaseName) {
        super(server);
        // todo: should check that this database exists.
        this.databaseName = databaseName; // todo: this could be a set database name looking for invalid strings? Unless the constructor already does this?
        Database database = new Database(databaseName);
        server.setCurrentDatabase(database);
    }
}
