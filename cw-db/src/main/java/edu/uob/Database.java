package edu.uob;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private String name;
    File folder;
    private ArrayList<Table> tables;

    public Database(String name) {
        String storageFolderPath = Paths.get("databases").toAbsolutePath().toString(); // this is a copy of the line in the server.
        String folderPath = storageFolderPath+File.separator+name;
        folder = new File(folderPath);
        this.name = name;
    }

    public void createDatabase(){
        // check if the directory already exists, if so throw an error.
        if(folder.exists()){
            //todo: error: folder already exists.
        }
        if(!folder.mkdir()){
            // todo: error: cannot make directory folder.
        }
    }

    public void dropDatabase(){
        // todo: if folder doesn't exist
        if(!folder.exists()){
            // todo: error: can't find database folder.
        }
        if(!folder.delete()){
            // todo: error: cannot delete database folder.
        }
    }

    public void useDatabase(){
        // todo: this will access the folder and create the underlying tables. It's kinda a constructor.

    }

    public List<Table> getTables() {
        return tables;
    }

    public Table getTableByName(String name){
        return null;
    }

    public void addTable() {

    }

    public void deleteTable() {

    }

}
