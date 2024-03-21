package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Table.ColNotFound;
import edu.uob.Exceptions.Table.NotFound;
import edu.uob.Model.Table;

import java.util.ArrayList;
import java.util.Collections;

public class SelectCommand extends Command {
    // select * from table
    public SelectCommand(DBServer server, String tableName) throws NotFound {
        super(server);
        this.tableName = tableName;
        Table t = server.getCurrentDatabase().getTableByName(this.tableName);
        if (t == null) {
            throw new NotFound(this.tableName);
        }
        this.returnString = "[OK]\n" + server.getCurrentDatabase().getTableByName(this.tableName).toString();
    }

    public SelectCommand(DBServer server, String tableName, ArrayList<String> columns, ArrayList<String> conditions) throws GenericException {
        super(server);
        this.tableName = tableName;
        this.conditions = conditions;
        this.colNames = columns;
        setTable(this.tableName);
        this.currentToken = 0;
        evalConditions(!columns.get(0).equals("*"), this.conditions);
    }


    // "(" <Condition> <BoolOperator> <Condition> ")" |
// <Condition> <BoolOperator> <Condition> |
// "(" [AttributeName] <Comparator> [Value] ")"
// | [AttributeName] <Comparator> [Value]
    private void evalConditions(boolean getCols, ArrayList<String> conditions) throws GenericException {
        if (conditions.isEmpty() && !getCols) {
            this.returnString = "[OK]\n" + table;
            return;
        }
        // your brackets have no power here.
        Table newTable;
        DeleteCommand.checkParenNumber(conditions);
        if (conditions.size() == 7) {
            if (conditions.get(3).equals("AND")) {
                Table tempTable = table.filterWithCondition(new ArrayList<>(conditions.subList(0, 3)));
                newTable = tempTable.filterWithCondition(new ArrayList<>(conditions.subList(4, conditions.size())));
            } else if (conditions.get(3).equals("OR")) {
                newTable = table.filterWithCondition(new ArrayList<>(conditions.subList(0, 3)));
                Table tempTable2 = table.filterWithCondition(new ArrayList<>(conditions.subList(4, conditions.size())));
                newTable.mergeTable(tempTable2);
            } else {
                throw new InvalidCommand("Expected AND or OR");
            }
        } else {
            newTable = table.filterWithCondition(conditions);
        }
        if (conditions.isEmpty()) {
            this.returnString = "[OK]\n" + newTable.getColumns(colNames);
        }
        if (getCols) {
            this.returnString = "[OK]\n" + newTable.getColumns(colNames);
        } else {
            this.returnString = "[OK]\n" + newTable.toString();
        }
    }


//     "SELECT " <AttribList> " FROM " [TableName]
    //  | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
}
