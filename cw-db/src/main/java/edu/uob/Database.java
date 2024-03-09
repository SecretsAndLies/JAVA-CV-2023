package edu.uob;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private String name;
    private List<Table> tables;

    public Database() {
        // todo: have a think about this.
    }

    public void createDatabase(String name){
        // check if the directory already exists, if so throw an error.

        // if not, create the folder
        // todo: should the initialization of arraylist be here?
        this.tables = new ArrayList<>();
    }

    public void dropDatabase(String name){
        // todo: if folder doesn't exist
    }

    public void useDatabase(String name){
        // todo: this is kinda like a constructor.
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
