package edu.uob.Model;

import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Database.AlreadyExists;
import edu.uob.Exceptions.Database.NotFound;
import edu.uob.Exceptions.GenericException;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String name;
    private File folder;
    private List<Table> tables;

    public Database(String name) {
        String storageFolderPath = Paths.get("databases").toAbsolutePath().toString(); // this is a copy of the line in the server.
        String folderPath = storageFolderPath+File.separator+name;
        folder = new File(folderPath);
        populateTables();
        this.name = name;
    }

    private void populateTables(){
        this.tables = new ArrayList<>();
        if(folder.exists()){
            File[] tableFiles = folder.listFiles();
            if(tableFiles==null){
                return;
            }
            for (File file : tableFiles){
                tables.add(new Table(file.getName()));
            }
        }
    }

    public void createDatabase() throws GenericException {
        if(folder.exists()){
            throw new AlreadyExists(folder.getName());
        }
        if(!folder.mkdir()){
            throw new InternalError();
        }
    }

    public boolean exists(){
        return folder.exists();
    }

    public void dropDatabase() throws GenericException {
        if(!folder.exists()){
            throw new NotFound(folder.getName());
        }
        if(!folder.delete()){
            throw new InternalError();
        }
    }

    public String getName() {
        return name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public Table getTableByName(String name){
        for (Table t : tables){
            if(t.getName().equals(name)){
                return t;
            }
        }
        return null;
    }

    public void addTable(Table table) {
        tables.add(table);
//        table.
        // todo: also write this table to output?
    }

    public void deleteTable(String name) throws edu.uob.Exceptions.Table.NotFound {
        for (Table t : tables){
            if(t.getName().equals(name)){
                t.delete();
                return;
            }
        }
        throw new edu.uob.Exceptions.Table.NotFound(name);
    }

}
