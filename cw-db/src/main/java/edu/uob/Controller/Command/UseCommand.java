package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Database.NotFound;
import edu.uob.Model.Database;

public class UseCommand extends Command {
    public UseCommand(DBServer server, String databaseName) throws NotFound, InternalError {
        super(server);
        this.databaseName = databaseName; // todo: this could be a set database name looking for invalid strings? Unless the constructor already does this?
        Database database = new Database(databaseName);
        if(!database.exists()){
            throw new NotFound(this.databaseName);
        }
        server.setCurrentDatabase(database);
    }
}
