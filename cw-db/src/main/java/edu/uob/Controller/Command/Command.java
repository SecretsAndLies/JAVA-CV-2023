package edu.uob.Controller.Command;

import edu.uob.DBServer;

import java.util.List;
import java.util.Set;

public abstract class Command {
    List<String> colNames;
    String tableName;
    String databaseName;
    List<String> valueList;
    String commandType;
    String returnString;
    DBServer server;

    public Command(DBServer server) {
        this.returnString = "";
        this.server = server;
    }

    public String getReturnString() {
        return returnString;
    }
}
