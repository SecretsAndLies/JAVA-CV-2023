package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Table.InsertionError;

import java.util.ArrayList;

public class InsertCommand extends Command {
    public InsertCommand(DBServer server, ArrayList<String> valuelist, String tableName) throws InternalError, InsertionError {
        super(server);
        this.tableName = tableName;
        // todo: check currentDB exists.
        this.valueList = valuelist;
        server.getCurrentDatabase().getTableByName(this.tableName).addRecord(this.valueList);
    }
}
