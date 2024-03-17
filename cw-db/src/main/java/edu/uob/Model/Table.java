package edu.uob.Model;


import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Table.*;
import edu.uob.Utils.Utils;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Table {
    private static Integer idIndex;
    private List<Record> records;
    private List<String> colNames;
    private String name;
    private Database database;
    private File file;

    public Table(String name, Database database) throws InternalError, InvalidName {
        idIndex = 1;
        this.name = createTableName(name);
        this.colNames = new ArrayList<>();
        this.records = new ArrayList<>();
        this.database = database;
        String filePath = database.getFolder() + File.separator + this.name + ".tab";
        this.file = new File(filePath);
        try {
            readFile();
        } catch (IOException e) {
            throw new InternalError();
        }
    }

    // an in memory table
//    public Table(ArrayList<String> colNames, ArrayList<ArrayList<String>> data){
//        this.colNames=colNames;
//        // loop through data. Create a record from the first item of each.
//        Record record = new Record(idIndex,)
//    }


    // reads the file into the object.
    private void readFile() throws IOException {
        if (!file.exists()) {
            return;
        }
        FileReader reader = new FileReader(this.file);
        BufferedReader buffReader = new BufferedReader(reader);
        String[] columnNames = buffReader.readLine().split("\t");
        this.colNames.addAll(Arrays.asList(columnNames));
        while (buffReader.ready()) {
            // todo: I do no validation on the files. They could have been modified in memory.
            String[] row = buffReader.readLine().split("\t");
            // this is nasty, it grabs the first col as the id.
            Record record = new Record(Integer.valueOf(row[0]), List.of(row).subList(1, row.length));
            this.records.add(record);
        }
        buffReader.close();
    }


    public Table(String name, List<String> colNames, Database database) throws InvalidName {
        idIndex = 1;
        this.name = createTableName(name);
        // todo validate the col names?
        colNames.add(0, "id");
        this.colNames = colNames;
        this.records = new ArrayList<>();
        // todo: duplicate of above constructor.
        String filePath = database.getFolder() + File.separator + this.name + ".tab";
        this.file = new File(filePath);
    }

    private String createTableName(String name) throws InvalidName {
        if (!Utils.isPlainText(name)) {
            throw new InvalidName();
        }        // and modify it if needed.
        return name.toLowerCase();
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        returnString.append(colNames.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\t")));
        returnString.append('\n');
        for (Record record : records) {
            returnString.append(record.toString());
        }
        return returnString.toString();
    }

    /* deletes the underlying table file.*/
    public void delete() throws NotFound {
        if (!this.file.delete()) {
            throw new NotFound(this.name);
        }
    }

    public String getName() {
        return name;
    }

    /* Saves the table in memory.*/
    public void createTable() throws AlreadyExists, InternalError {
        if (this.file.exists()) {
            throw new AlreadyExists(name);
        }
        try {
            if (!this.file.createNewFile()) {
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
        if (valueList.size() + 1 != this.colNames.size()) {
            throw new InsertionError("Trying to too many or two few columns.");
        }
        for (int i = 0; i < valueList.size(); i++) {
            String value = valueList.get(i);
            if (value.startsWith("'") && value.endsWith("'")) {
                valueList.set(i, value.replace("'", ""));
            }
        }
        Record record = new Record(idIndex, valueList);
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

    public void addCol(String colName) throws InvalidCommand, InternalError {
        // todo: duplicate colname handling?
        if (!Utils.isPlainText(colName)) {
            throw new InvalidCommand("column name is invalid");
        }
        colNames.add(colName);
        for (Record r : records) {
            r.addCol();
        }
        this.saveTable();
    }

    public void removeCol(String colName) throws ColNotFound, InternalError {
        int index = colNames.indexOf(getCaseSensitiveColName(colName));
        colNames.remove(colName);
        for (Record r : records) {
            r.removeCol(index);
        }
        this.saveTable();

    }

    public String getColumns(List<String> colNames) throws ColNotFound {
        List<List<String>> cols = new ArrayList<>();
        for (String colName : colNames) {
            cols.add(getCol(colName));
        }
        String colsAsString = "";

        for (int i = 0; i < cols.get(0).size(); i++) {
            for (int j = 0; j < cols.size(); j++) {
                colsAsString += cols.get(j).get(i) + "\t";
            }
            colsAsString += "\n";
        }
        return colsAsString;
    }

    private String getCaseSensitiveColName(String name) throws ColNotFound {
        boolean found = false;
        String caseSensitiveName = "";
        for (String colName : colNames) {
            if (colName.equalsIgnoreCase(name)) {
                found = true;
                caseSensitiveName = colName;
                break;
            }
        }
        if (!found) {
            throw new ColNotFound();
        }
        return caseSensitiveName;
    }


    public List<String> getCol(String name) throws ColNotFound {
        String caseSensitiveName = getCaseSensitiveColName(name);
        List<String> col = new ArrayList<>();
        col.add(caseSensitiveName);
        int index = colNames.indexOf(caseSensitiveName);
        for (Record record : records) {
            col.add(record.getByIndex(index));
        }
        return col;
    }
}

/*
   You should treat column names as case insensitive for querying, but you should
   preserve the case when storing them (i.e. do NOT convert them to lower case).
   This is so that the user can define attribute names using CamelCase if they
   wish (which is a useful aid to readability).

 */