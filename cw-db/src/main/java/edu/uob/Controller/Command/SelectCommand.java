package edu.uob.Controller.Command;

import edu.uob.DBServer;

public class SelectCommand extends Command {
    // select * from table
    public SelectCommand(DBServer server, String tableName) {
        super(server);
        this.tableName = tableName;
        // todo: what happens when this gives an error
        this.returnString = "[OK]" + server.getCurrentDatabase().getTableByName(this.tableName).toString();
    }
//     "SELECT " <AttribList> " FROM " [TableName]
    //  | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
}
