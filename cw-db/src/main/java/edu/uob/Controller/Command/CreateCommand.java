package edu.uob.Controller.Command;

import edu.uob.DBServer;
import edu.uob.Exceptions.Command.MustIncludeDBOrTable;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Database.ReservedKeyword;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Table.AlreadyExists;
import edu.uob.Model.Database;
import edu.uob.Model.Table;

import java.util.ArrayList;
import java.util.HashSet;

import static edu.uob.Utils.Utils.isReservedKeyword;

public class CreateCommand extends Command {
    public CreateCommand(DBServer server, String location, String name) throws GenericException {
        super(server);
        if(isReservedKeyword(name)){
            throw new ReservedKeyword(name);
        }
        if(location.equals("DATABASE")) {
            this.databaseName=name;
            new Database(databaseName).createDatabase();
        }
        else if(location.equals("TABLE")){
            createTable(name);
        }
        else {
            throw new MustIncludeDBOrTable();
        }
    }

    // this is constructor for tables with columns.
    public CreateCommand(DBServer server, String name, ArrayList<String> attributeList) throws AlreadyExists {
        super(server);
//        Database db = this.server.getCurrentDatabase();
//        this.colNames=new ArrayList<>();
//            for (String s : attributeList){
//            // todo: validate col names
//            if(colNames.contains(s)) {
//                // todo: throw duplicate col names.
//            }
//            colNames.add(s);
//        }
//        Table t = new Table(name, db, colNames);
    }

    private void createTable(String name) throws AlreadyExists, InternalError {
        Database db = this.server.getCurrentDatabase();
        Table t = new Table(name, db);
        if(db.getTableByName(name)!=null){
            throw new AlreadyExists(name);
        }
        db.addTable(t);
    }


}
