package edu.uob;


import java.util.ArrayList;

public class Table {
    private int idIndex;
    private ArrayList<Record> records;

    public Table() {
        idIndex = 1;
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