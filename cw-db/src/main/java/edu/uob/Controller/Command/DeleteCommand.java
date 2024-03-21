package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Table.NotFound;
import edu.uob.Model.Table;

import java.util.ArrayList;
import java.util.Collections;

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
        checkParenNumber(conditions);
        Table newTable;
        if (conditions.size() == 7) {
            if (conditions.get(3).equals("AND")) {
                Table tempTable = table.filterWithCondtionReverse(new ArrayList<>(conditions.subList(0, 3)));
                newTable = tempTable.filterWithCondtionReverse(new ArrayList<>(conditions.subList(4, conditions.size())));
            } else if (conditions.get(3).equals("OR")) {
                newTable = table.filterWithCondtionReverse(new ArrayList<>(conditions.subList(0, 3)));
                Table tempTable2 = table.filterWithCondtionReverse(new ArrayList<>(conditions.subList(4, conditions.size())));
                newTable.mergeTable(tempTable2);
            } else {
                throw new InvalidCommand("Expected AND or OR");
            }
        } else {
            newTable = table.filterWithCondtionReverse(conditions);
        }
        table.setRecords(newTable.getRecords());
        returnString = "[OK]\n";
    }

    static void checkParenNumber(ArrayList<String> conditions) throws InvalidCommand {
        if (Collections.frequency(conditions, ")") != Collections.frequency(conditions, "(")) {
            throw new InvalidCommand("Wrong number of parenthesis.");
        }
        conditions.removeIf(e -> e.equals(")"));
        conditions.removeIf(e -> e.equals("("));
    }
}
