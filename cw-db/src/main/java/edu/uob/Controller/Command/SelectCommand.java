package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Table.ColNotFound;
import edu.uob.Exceptions.Table.NotFound;
import edu.uob.Model.Table;

import java.util.ArrayList;

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

    public SelectCommand(DBServer server, String tableName, ArrayList<String> columns) throws NotFound, ColNotFound {
        super(server);
        this.tableName = tableName;
        Table t = server.getCurrentDatabase().getTableByName(this.tableName);
        if (t == null) {
            throw new NotFound(this.tableName);
        }
        this.returnString = "[OK]\n" + t.getColumns(columns);
    }

//     "SELECT " <AttribList> " FROM " [TableName]
    //  | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
}
