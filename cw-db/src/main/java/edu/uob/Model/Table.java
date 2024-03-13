package edu.uob.Model;


import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Table.AlreadyExists;
import edu.uob.Exceptions.Table.InsertionError;
import edu.uob.Exceptions.Table.NotFound;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table {
    private static Integer idIndex;
    private List<Record> records;
    private List<String> colNames;
    private String name;
    private Database database;
    private File file;

    public Table(String name, Database database) {
        idIndex = 1;
        this.name=createTableName(name);
        this.colNames=new ArrayList<>();
        this.records=new ArrayList<>();
        // todo check if exists and if so read the stuff from memory?
        String filePath = database.getFolder() + File.separator+ this.name + ".tab";
        this.file = new File(filePath);
    }

    public Table(String name, List<String> colNames, Database database){
        idIndex = 1;
        this.name=createTableName(name);
        // todo validate the col names?
        colNames.add(0,"id");
        this.colNames=colNames;
        this.records=new ArrayList<>();
        // todo: duplicate of above constructor.
        String filePath = database.getFolder() + File.separator+ this.name + ".tab";
        this.file = new File(filePath);
    }

    private String createTableName(String name){
        // todo validate that the name is valid (no spaces or whatever)?
        // and modify it if needed.
        return name;
    }
    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        returnString.append(colNames.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\t")));
        returnString.append('\n');
        for (Record record : records){
            returnString.append(record.toString());
        }
        return returnString.toString();
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
            if(!this.file.createNewFile()){
                throw new InternalError();
            }
        } catch (IOException e) {
            throw new InternalError();
        }
        saveTable();
    }
    // adds the record to the table and increments the id index.
    public void addRecord(List<String> valueList) throws InternalError, InsertionError {
        // todo check valid valuelist (right number of cols etc in table.)
        if(valueList.size()+1 != this.colNames.size()){
            throw new InsertionError("Trying to too many or two few columns.");
        }
        for(int i=0; i<valueList.size(); i++){
            String value = valueList.get(i);
            if(value.startsWith("'") && value.endsWith("'")){
                valueList.set(i,value.replace("'",""));
            }
        }
        Record record = new Record(idIndex,valueList);
        this.records.add(record);
        saveTable();
        idIndex++;
    }

    // saves the table onto disk.
    public void saveTable() throws InternalError {
// if there are an colNames then write them to the file.
        String toWrite = this.toString();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(toWrite);
        } catch (IOException e) {
            throw new InternalError();
        }
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