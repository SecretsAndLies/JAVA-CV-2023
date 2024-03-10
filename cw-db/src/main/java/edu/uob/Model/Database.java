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
    private ArrayList<Table> tables;

    public Database(String name) {
        String storageFolderPath = Paths.get("databases").toAbsolutePath().toString(); // this is a copy of the line in the server.
        String folderPath = storageFolderPath+File.separator+name;
        folder = new File(folderPath);
        this.name = name;
    }

    public void createDatabase() throws GenericException {
        if(folder.exists()){
            throw new AlreadyExists(folder.getName());
        }
        if(!folder.mkdir()){
            throw new InternalError();
        }
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
        return null;
    }

    public void addTable() {

    }

    public void deleteTable() {

    }

}
