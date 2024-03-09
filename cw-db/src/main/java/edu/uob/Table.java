package edu.uob;


public class Table {
    private int idIndex;

    public Table() {
        idIndex = 1;
    }

    public void addRecord() {

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