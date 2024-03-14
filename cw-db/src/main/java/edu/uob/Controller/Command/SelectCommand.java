package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Table.NotFound;
import edu.uob.Model.Table;

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
//     "SELECT " <AttribList> " FROM " [TableName]
    //  | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
}
