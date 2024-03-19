package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Table.NotFound;
import edu.uob.Model.Table;

import java.util.ArrayList;

public class DeleteCommand extends Command {

    public DeleteCommand(DBServer server, String tableName, String colName, ArrayList<String> conditions) throws GenericException {
        super(server);
        this.tableName = tableName;
        this.conditions = conditions;
        this.colName = colName;
        setTable(this.tableName);
        evalDelete();

    }

    private void evalDelete() throws GenericException {
        if (conditions.isEmpty()) {
            throw new InvalidCommand("delete command must include conditions.");
        }
        Table newTable = table.filterWithCondtionReverse(conditions);
        table.setRecords(newTable.getRecords());
        returnString = "[OK]\n";
    }
}
