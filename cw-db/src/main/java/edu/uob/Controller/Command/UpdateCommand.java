package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Table.NotFound;

import java.util.ArrayList;

public class UpdateCommand extends Command {

    public UpdateCommand(DBServer server, ArrayList<String> conditions, String tableName, ArrayList<String> nameValueList) throws GenericException {
        super(server);
        setTable(tableName);
        this.conditions = conditions;
        this.valueList = nameValueList;
        evalupdate();
    }

    private void evalupdate() throws GenericException {
        if (conditions.isEmpty()) {
            throw new InvalidCommand("update command must include conditions.");
        }
        if (valueList.isEmpty()) {
            throw new InvalidCommand("update command must include columns and data to update..");
        }

        if (!this.valueList.get(1).equals("=")) {
            throw new InvalidCommand("Expected =");
        }

        String colToUpdate = this.valueList.get(0);
        String value = this.valueList.get(2);

        this.table = this.table.updateWithConditions(conditions, colToUpdate, value);

    }
}
