package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Table.NotFound;

import java.util.ArrayList;
import java.util.List;

public class UpdateCommand extends Command {

    public UpdateCommand(DBServer server, ArrayList<String> conditions, String tableName, ArrayList<String> nameValueList) throws GenericException {
        super(server);
        setTable(tableName);
        this.conditions = conditions;
        this.valueList = nameValueList;
        evalupdate(this.valueList);
    }

    private void evalupdate(List<String> nameValueList) throws GenericException {
        if (conditions.isEmpty()) {
            throw new InvalidCommand("update command must include conditions.");
        }
        if (nameValueList.isEmpty()) {
            throw new InvalidCommand("update command must include columns and data to update..");
        }
        if (nameValueList.size() < 3) {
            // you might be at the end of the query.
            return;
        }

        if (!nameValueList.get(1).equals("=")) {
            throw new InvalidCommand("Expected =");
        }

        String colToUpdate = nameValueList.get(0);
        String value = nameValueList.get(2).replace("'", "");

        this.table = this.table.updateWithConditions(conditions, colToUpdate, value);
        if (nameValueList.size() > 3 && (nameValueList.get(3).equals(","))) {
            evalupdate(nameValueList.subList(4, nameValueList.size()));
        }
    }
}
