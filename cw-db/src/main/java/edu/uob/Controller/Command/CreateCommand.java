package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.MustIncludeDBOrTable;
import edu.uob.Exceptions.Database.ReservedKeyword;
import edu.uob.Exceptions.GenericException;
import edu.uob.Model.Database;

import static edu.uob.Utils.Utils.isReservedKeyword;

public class CreateCommand extends Command {
    public CreateCommand(DBServer server, String location, String DBName) throws GenericException {
        super(server);
        if(isReservedKeyword(DBName)){
            throw new ReservedKeyword(DBName);
        }
        this.databaseName=DBName;
        if(location.equals("DATABASE")) {
            new Database(databaseName).createDatabase();
        }
        else if(location.equals("TABLE")){
            // todo: implement.
        }
        else {
            throw new MustIncludeDBOrTable();
        }

    }
}
