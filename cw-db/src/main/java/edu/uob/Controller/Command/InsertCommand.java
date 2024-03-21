package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Table.InsertionError;
import edu.uob.Model.Database;

import java.util.ArrayList;

public class InsertCommand extends Command {
    public InsertCommand(DBServer server, ArrayList<String> valuelist, String tableName) throws InternalError, InsertionError, InvalidCommand {
        super(server);
        this.tableName = tableName;
        this.valueList = valuelist;
        server.getCurrentDatabase().getTableByName(this.tableName).addRecord(this.valueList);
    }
}
