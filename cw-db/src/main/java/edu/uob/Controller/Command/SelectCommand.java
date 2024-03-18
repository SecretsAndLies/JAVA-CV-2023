package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Table.ColNotFound;
import edu.uob.Exceptions.Table.InvalidName;
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

    public SelectCommand(DBServer server, String tableName, ArrayList<String> columns, ArrayList<String> conditions) throws NotFound, ColNotFound, InternalError, InvalidName, InvalidCommand {
        super(server);
        this.tableName = tableName;
        Table t = server.getCurrentDatabase().getTableByName(this.tableName);
        if (t == null) {
            throw new NotFound(this.tableName);
        }
        // todo this would validate too long lists (And is repetitive from above and in the parser method.)
        //  Ideally you'd have one constructor rather than three and do the evaluation here.
        if (columns.get(0).equals("*")) {
            if (conditions.isEmpty()) {
                this.returnString = "[OK]\n" + t;
            } else {
                Table newTable = t.filterWithCondtion(conditions);
                this.returnString = "[OK]\n" + newTable.toString();
            }
        } else {
            // todo: eventually need to do the conditions on this too.
            this.returnString = "[OK]\n" + t.getColumns(columns);
        }

    }

//     "SELECT " <AttribList> " FROM " [TableName]
    //  | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
}
