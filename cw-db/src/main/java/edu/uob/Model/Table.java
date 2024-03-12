package edu.uob.Model;


import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Table.AlreadyExists;
import edu.uob.Exceptions.Table.NotFound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Table {
    private static Integer idIndex;
    private ArrayList<Record> records;
    private ArrayList<String> colNames;
    private String name;
    private Database database;
    private File file;

    public Table(String name, Database database) {
        idIndex = 1;
        this.name=createTableName(name);
        this.colNames=new ArrayList<>();
        this.records=new ArrayList<>();
        // todo check if exists?
        String filePath = database.getFolder() + File.separator+ this.name + ".tab";
        this.file = new File(filePath);
    }

    private String createTableName(String name){
        // todo any modifcation needed of the table name?
        return name;
    }

    public Table(String name, ArrayList<String> colNames, Database database){
        idIndex = 1;
        // todo validate the col names?
        this.name=createTableName(name);
        this.colNames=new ArrayList<>();
        this.records=new ArrayList<>();

    }

    /* deletes the underlying table file.*/
    public void delete() throws NotFound {
        if(!this.file.delete()){
            throw new NotFound(this.name);
        }
    }

    public String getName() {
        return name;
    }

    /* Saves the table in memory.*/
    public void createTable() throws AlreadyExists, InternalError {
        if(this.file.exists()){
            throw new AlreadyExists(name);
        }
        try {
            this.file.createNewFile();
        } catch (IOException e) {
            throw new InternalError();
        }
    }

    // todo: have a think about how records of arbitrary size should be passed into this method.
    // my guess is I'll need a record type?
    public void addRecord() {
        // adds the record to the table and increments the id index.
        idIndex++;
    }

    public void getRecords(){
        // fetches the specified records according to the filtering criteria
        // and adds to the return string?
    }

    public void modifyRecord(){
        // searches for a specific record and changes the specified col
    }

    public void addCollumn() {
    }
}

// within the table, we have a list of  key value pairs.
// eg: "id" : DatabaseObject

/*
Potentially this should be a toFile method that I get from another class?
Both Database names and Table names should be case insensitive
 (since some file systems have problems differentiating between
  upper and lower case filenames). Any database/table names provided
   by the user should be converted into lowercase before saving out to the filesystem.
   You should treat column names as case insensitive for querying, but you should
   preserve the case when storing them (i.e. do NOT convert them to lower case).
   This is so that the user can define attribute names using CamelCase if they
   wish (which is a useful aid to readability).

   You should treat column names as case insensitive for querying,
   but you should preserve the case when storing them
    (i.e. do NOT convert them to lower case).
    This is so that the user can define attribute names using
    CamelCase if they wish (which is a useful aid to readability).


 */