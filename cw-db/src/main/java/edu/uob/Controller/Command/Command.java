package edu.uob.Controller.Command;

import edu.uob.DBServer;

import java.util.List;
import java.util.Set;

public abstract class Command {
    List<Condition> conditions;
    Set<String> colNames;
    Set<String> tableNames;
    String databaseName;
    String commandType;
    String returnString;

    public Command(DBServer server){
        this.returnString="";
    }

    public String getReturnString() {
        return returnString;
    }
}
