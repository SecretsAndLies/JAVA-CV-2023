package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Table.ColNotFound;
import edu.uob.Exceptions.Table.InvalidName;
import edu.uob.Model.Table;

public class AlterCommand extends Command {

    public AlterCommand(DBServer server, String tableName, String addOrDrop, String colName) throws InvalidName, InternalError, InvalidCommand, ColNotFound {
        super(server);
        Table t = server.getCurrentDatabase().getTableByName(tableName);
        if (addOrDrop.equals("ADD")) {
            t.addCol(colName);
        } else if (addOrDrop.equals("DROP")) {
            t.removeCol(colName);
        } else {
            throw new InvalidCommand("ADD or DROP expected.");
        }
    }
}
