package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Table.NotFound;
import edu.uob.Model.Table;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {
    List<String> colNames;
    String tableName;
    String databaseName;
    String colName;
    List<String> valueList;
    String returnString;
    DBServer server;
    ArrayList<String> conditions;
    Table table;

    public Command(DBServer server) {
        this.returnString = "";
        this.server = server;
    }

    public String getReturnString() {
        return returnString;
    }

    public void setTable(String tableName) throws NotFound {
        Table t = server.getCurrentDatabase().getTableByName(tableName);
        this.table = t;
        if (t == null) {
            throw new NotFound(this.tableName);
        }
    }
}
