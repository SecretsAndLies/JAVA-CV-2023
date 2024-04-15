package edu.uob.Model;


import edu.uob.Exceptions.Command.InvalidCommand;
import edu.uob.Exceptions.Database.InternalError;
import edu.uob.Exceptions.Database.ReservedKeyword;
import edu.uob.Exceptions.GenericException;
import edu.uob.Exceptions.Table.*;
import edu.uob.Utils.Utils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static edu.uob.Utils.Utils.isNumeric;

public class Table {
    private static Integer idIndex;
    private List<Record> records;
    private final List<String> colNames;
    private final String name;
    private Database database;
    private final File file;

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

    public static void setIdIndex(int idIndex) {
        Table.idIndex = idIndex;
    }

    public List<String> getColNames() {
        return colNames;
    }

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

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public Table(String name, List<String> colNames, Database database) throws InvalidName, InvalidCommand, ReservedKeyword {
        idIndex = 1;
        this.name = createTableName(name);
        Set<String> cleanCols = new HashSet<>();
        for (String colName : colNames) {
            if (cleanCols.contains(colName.toLowerCase())) {
                throw new InvalidCommand("Contains duplicate column.");
            }
            if (Utils.isReservedKeyword(colName)) {
                throw new ReservedKeyword(colName);
            }
            cleanCols.add(colName);
        }
        colNames.add(0, "id");
        this.colNames = colNames;
        this.records = new ArrayList<>();
        String filePath = database.getFolder() + File.separator + this.name + ".tab";
        this.file = new File(filePath);
    }

    private String createTableName(String name) throws InvalidName {
        if (!Utils.isPlainText(name)) {
            throw new InvalidName();
        }
        if (Utils.isReservedKeyword(name)) {
            throw new InvalidName();
        }
        // todo modify it if needed.
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

    public void secretAddRecord(List<String> valueList) throws InvalidCommand {
        for (int i = 0; i < valueList.size(); i++) {
            String value = valueList.get(i);
            if (value.startsWith("'") && value.endsWith("'")) {
                valueList.set(i, value.replace("'", ""));
            }
        }
        Record record = new Record(idIndex, valueList);
        this.records.add(record);
        idIndex++;
    }

    // adds the record to the table and increments the id index.
    public void addRecord(List<String> valueList) throws InternalError, InsertionError, InvalidCommand {
        if (valueList.size() + 1 != this.colNames.size()) {
            throw new InsertionError("Trying to too many or two few columns.");
        }
        replaceStringQuotes(valueList);
        saveTable();
        idIndex++;
    }

    private void replaceStringQuotes(List<String> valueList) throws InvalidCommand {
        for (int i = 0; i < valueList.size(); i++) {
            String value = valueList.get(i);
            if (value.startsWith("'") && value.endsWith("'")) {
                valueList.set(i, value.replace("'", ""));
            } else {
                boolean isDouble;
                try {
                    double d = Double.parseDouble(value);
                    isDouble = true;
                } catch (NumberFormatException nfe) {
                    isDouble = false;
                }

                if (!(value.equals("TRUE") || value.equals("FALSE") || isDouble)) {
                    throw new InvalidCommand("Value should be a quoted string or TRUE, FALSE or a number.");
                }
            }
        }
        Record record = new Record(idIndex, valueList);
        this.records.add(record);
    }

    // creates an in memory table with the given records.
    public Table(List<String> cols, Database db, List<Record> records) {
        this.records = records;
        this.colNames = cols;
        this.database = db;
        this.name = "temp";
        this.file = null;
    }

    private void checkConditionLength(ArrayList<String> condition) throws InternalError {
        if (condition.size() != 3) {
            throw new InternalError("Multiple conditions not supported");
        }
    }
    // creates a new table that's filter
    public Table filterWithCondition(ArrayList<String> condition) throws InternalError, InvalidCommand {
        Table table = new Table(this.colNames, this.database, new ArrayList<>(records));
        if (condition.isEmpty()) {
            return table;
        }
        checkConditionLength(condition);
        String colName = condition.get(0);
        String operator = condition.get(1);
        String value = condition.get(2);
        if (value.contains("'")) {
            value = value.replace("'", "");
        }
        int colIndex = table.colNames.indexOf(colName.toLowerCase());
        if (colIndex == -1) {
            throw new InvalidCommand("Column doesn't exist.");
        }
        String finalValue = value;
        executeFilterWithCondition(operator,colIndex,finalValue,table);
        return table;
    }

    private void executeFilterWithCondition (String operator, int colIndex, String finalValue, Table table) throws InvalidCommand {
        switch (operator) {
            case "==" -> table.records.removeIf(record -> !record.getByIndex(colIndex).equals(finalValue));
            case "!=" -> table.records.removeIf(record -> record.getByIndex(colIndex).equals(finalValue));
            case "LIKE" -> table.records.removeIf(record -> !record.getByIndex(colIndex).contains(finalValue));
            case "<" -> table.records.removeIf(record -> {
                String recordValue = record.getByIndex(colIndex);
                if (isNumeric(recordValue) && isNumeric(finalValue)) {
                    return !(Float.parseFloat(recordValue) < Float.parseFloat(finalValue));
                }
                return true;
            });
            case ">" -> table.records.removeIf(record -> {
                String recordValue = record.getByIndex(colIndex);
                if (isNumeric(recordValue) && isNumeric(finalValue)) {
                    return !(Float.parseFloat(recordValue) > Float.parseFloat(finalValue));
                }
                return true;
            });
            case "<=" -> table.records.removeIf(record -> {
                String recordValue = record.getByIndex(colIndex);
                if (isNumeric(recordValue) && isNumeric(finalValue)) {
                    return !(Float.parseFloat(recordValue) <= Float.parseFloat(finalValue));
                }
                return true;
            });
            case ">=" -> table.records.removeIf(record -> {
                String recordValue = record.getByIndex(colIndex);
                if (isNumeric(recordValue) && isNumeric(finalValue)) {
                    return !(Float.parseFloat(recordValue) >= Float.parseFloat(finalValue));
                }
                return true;
            });
            default -> throw new InvalidCommand("Invalid operator.");
        }
    }

    private void executeFilterWithConditionReverse (String operator, int colIndex, String finalValue, Table table) throws InvalidCommand {
        switch (operator) {
            case "==" -> table.records.removeIf(record -> record.getByIndex(colIndex).equals(finalValue));
            case "!=" -> table.records.removeIf(record -> !record.getByIndex(colIndex).equals(finalValue));
            case "LIKE" -> table.records.removeIf(record -> record.getByIndex(colIndex).contains(finalValue));
            case "<" -> table.records.removeIf(record -> {
                String recordValue = record.getByIndex(colIndex);
                if (isNumeric(recordValue) && isNumeric(finalValue)) {
                    return (Float.parseFloat(recordValue) < Float.parseFloat(finalValue));
                }
                return false;
            });
            case ">" -> table.records.removeIf(record -> {
                String recordValue = record.getByIndex(colIndex);
                if (isNumeric(recordValue) && isNumeric(finalValue)) {
                    return (Float.parseFloat(recordValue) > Float.parseFloat(finalValue));
                }
                return false;
            });
            case "<=" -> table.records.removeIf(record -> {
                String recordValue = record.getByIndex(colIndex);
                if (isNumeric(recordValue) && isNumeric(finalValue)) {
                    return (Float.parseFloat(recordValue) <= Float.parseFloat(finalValue));
                }
                return false;
            });
            case ">=" -> table.records.removeIf(record -> {
                String recordValue = record.getByIndex(colIndex);
                if (isNumeric(recordValue) && isNumeric(finalValue)) {
                    return (Float.parseFloat(recordValue) >= Float.parseFloat(finalValue));
                }
                return false;
            });
            default -> throw new InvalidCommand("Invalid operator.");
        }
    }


    public Table filterWithCondtionReverse(ArrayList<String> condition) throws InternalError, InvalidName, InvalidCommand {
        Table table = new Table(this.colNames, this.database, new ArrayList<>(records));
        if (condition.isEmpty()) {
            return table;
        }
        if (condition.size() != 3) {
            throw new InternalError("Multiple conditions not supported");
        }
        String colName = condition.get(0);
        String operator = condition.get(1);
        String value = condition.get(2);
        if (value.contains("'")) {
            value = value.replace("'", "");
        }
        int colIndex = table.colNames.indexOf(colName);
        if (colIndex == -1) {
            throw new InvalidCommand("Column doesn't exist.");
        }
        String finalValue = value;
        executeFilterWithConditionReverse(operator,colIndex,finalValue,table);
        return table;
    }

    private void updateEquals(int colIndex, String finalValue,
                              Table table, int colToUpdateIndex, String valueToUpdate){
        for (Record record : table.records) {
            if (record.getByIndex(colIndex).equals(finalValue)) {
                record.setColVal(colToUpdateIndex, valueToUpdate);
            }
        }
    }

    private void updateNotEquals(int colIndex, String finalValue,
                                 Table table, int colToUpdateIndex, String valueToUpdate){
        for (Record record : table.records) {
            if (!record.getByIndex(colIndex).equals(finalValue)) {
                record.setColVal(colToUpdateIndex, valueToUpdate);
            }
        }
    }
    private void updateLike(int colIndex, String finalValue,
                                 Table table, int colToUpdateIndex, String valueToUpdate) {
        for (Record record : table.records) {
            if (record.getByIndex(colIndex).contains(finalValue)) {
                record.setColVal(colToUpdateIndex, valueToUpdate);
            }
        }
    }
    private void updateLT(int colIndex, String finalValue,
                            Table table, int colToUpdateIndex, String valueToUpdate) {
        for (Record record : table.records) {
            String recordValue = record.getByIndex(colIndex);
            if (isNumeric(recordValue) && isNumeric(finalValue) &&
                    (Float.parseFloat(recordValue) < Float.parseFloat(finalValue))) {
                record.setColVal(colToUpdateIndex, valueToUpdate);
            }
        }
    }
    private void updateGT(int colIndex, String finalValue,
                          Table table, int colToUpdateIndex, String valueToUpdate) {
        for (Record record : table.records) {
            String recordValue = record.getByIndex(colIndex);
            if (isNumeric(recordValue) && isNumeric(finalValue) &&
                    (Float.parseFloat(recordValue) > Float.parseFloat(finalValue))) {
                record.setColVal(colToUpdateIndex, valueToUpdate);
            }
        }
    }
    private void updateGTEqual(int colIndex, String finalValue,
                          Table table, int colToUpdateIndex, String valueToUpdate) {
        for (Record record : table.records) {
            String recordValue = record.getByIndex(colIndex);
            if (isNumeric(recordValue) && isNumeric(finalValue) &&
                    (Float.parseFloat(recordValue) >= Float.parseFloat(finalValue))) {
                record.setColVal(colToUpdateIndex, valueToUpdate);
            }
        }
    }
    private void updateLTEqual(int colIndex, String finalValue,
                          Table table, int colToUpdateIndex, String valueToUpdate) {
        for (Record record : table.records) {
            String recordValue = record.getByIndex(colIndex);
            if (isNumeric(recordValue) && isNumeric(finalValue) &&
                    (Float.parseFloat(recordValue) <= Float.parseFloat(finalValue))) {
                record.setColVal(colToUpdateIndex, valueToUpdate);
            }
        }
    }

    private void executeUpdateWithCondtions (String operator, int colIndex, String finalValue,
                                             Table table, int colToUpdateIndex, String valueToUpdate)
            throws InvalidCommand {
        switch (operator) {
            case "==" -> {
                updateEquals(colIndex,finalValue,table,colToUpdateIndex,valueToUpdate);
            }
            case "!=" -> {
                updateNotEquals(colIndex,finalValue,table,colToUpdateIndex,valueToUpdate);
            }
            case "LIKE" -> {
                updateLike(colIndex,finalValue,table,colToUpdateIndex,valueToUpdate);
            }
            case "<" -> {
                updateLT(colIndex,finalValue,table,colToUpdateIndex,valueToUpdate);
            }
            case ">" -> {
                updateGT(colIndex,finalValue,table,colToUpdateIndex,valueToUpdate);
            }
            case "<=" -> {
                updateLTEqual(colIndex,finalValue,table,colToUpdateIndex,valueToUpdate);
            }
            case ">=" -> {
                updateGTEqual(colIndex,finalValue,table,colToUpdateIndex,valueToUpdate);
            }
            default -> throw new InvalidCommand("Invalid operator.");
        }
    }

    private void checkCondition(ArrayList<String> condition) throws InternalError {
        if (condition.size() != 3) {
            throw new InternalError("Multiple conditions not supported");
        }
    }
    public Table updateWithConditions(ArrayList<String> condition, String colToUpdate, String valueToUpdate) throws GenericException {
        Table table = new Table(this.colNames, this.database, new ArrayList<>(records));
        if (condition.isEmpty()) {
            return table;
        }
        checkCondition(condition);
        String colName = condition.get(0);
        String operator = condition.get(1);
        String value = condition.get(2);
        if (value.contains("'")) {
            value = value.replace("'", "");
        }
        int colIndex = table.colNames.indexOf(colName);
        int colToUpdateIndex = table.colNames.indexOf(colToUpdate);

        if (colIndex == -1 || colToUpdateIndex == -1) {
            throw new InvalidCommand("Column doesn't exist.");
        }
        String finalValue = value;
        executeUpdateWithCondtions ( operator,  colIndex,  finalValue,
                 table,  colToUpdateIndex,  valueToUpdate);
        return table;
    }

    public Table(ArrayList<String> colNames) {
        this.colNames = colNames;
        this.records = new ArrayList<>();
        this.name = null;
        this.file = null;
    }

    public int getColIndex(String searchCol) {
        int rightColIndex = -1;
        for (int i = 0; i < this.colNames.size(); i++) {
            if (this.colNames.get(i).equalsIgnoreCase(searchCol)) {
                rightColIndex = i;
                break;
            }
        }
        return rightColIndex;
    }

    private void executeJoin(Table otherTable, int leftColIndex, int rightColIndex, Table joinTable) throws InvalidCommand {
        for (Record record : this.records) {
            for (Record rightRecord : otherTable.getRecords()) {
                if (record.getByIndex(leftColIndex).equals(rightRecord.getByIndex(rightColIndex))) {
                    ArrayList<String> leftRecordData = record.copyData();
                    ArrayList<String> rightRecordData = rightRecord.copyData();
                    if (leftColIndex == 0) {
                        leftRecordData.remove(leftColIndex);
                    } else {
                        leftRecordData.remove(leftColIndex);
                        leftRecordData.remove(0);
                    }
                    if (rightColIndex == 0) {
                        rightRecordData.remove(rightColIndex);
                    } else {
                        rightRecordData.remove(rightColIndex);
                        rightRecordData.remove(0);
                    }
                    leftRecordData.addAll(rightRecordData);
                    joinTable.secretAddRecord(leftRecordData);
                }
            }
        }
    }

    public Table joinTable(Table otherTable, String leftCol, String rightCol) throws GenericException {
        ArrayList<String> returnColNames = new ArrayList<>();
        // we minus 1 because we remove the id.
        int leftColIndex = this.getColIndex(leftCol);
        int rightColIndex = otherTable.getColIndex(rightCol);
        returnColNames.add("id");
        for (String name : this.colNames) {
            if (name.equals("id") || name.equals(leftCol)) {
                continue;
            }
            returnColNames.add(this.name + "." + name);
        }
        for (String name : otherTable.getColNames()) {
            if (name.equals("id") || name.equals(rightCol)) {
                continue;
            }
            returnColNames.add(otherTable.name + "." + name);
        }
        Table joinTable = new Table(returnColNames);
        executeJoin(otherTable,  leftColIndex,  rightColIndex,  joinTable);
        return joinTable;
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
        if (containsColNonCaseSensitive(colName)) {
            throw new InvalidCommand("trying to add a duplicative column name.");
        }
        if (!Utils.isPlainText(colName)) {
            throw new InvalidCommand("column name is invalid");
        }
        colNames.add(colName);
        for (Record r : records) {
            r.addCol();
        }
        this.saveTable();
    }

    public void mergeTable(Table otherTable) {
        for (Record r : otherTable.records) {
            if (!this.records.contains(r)) {
                this.records.add(r);
            }
        }
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
        StringBuilder colsAsString = new StringBuilder();

        for (int i = 0; i < cols.get(0).size(); i++) {
            for (List<String> col : cols) {
                colsAsString.append(col.get(i)).append("\t");
            }
            colsAsString.append("\n");
        }
        return colsAsString.toString();
    }

    private boolean containsColNonCaseSensitive(String name) {
        boolean found = false;
        for (String colName : colNames) {
            if (colName.equalsIgnoreCase(name)) {
                found = true;
                break;
            }
        }
        return found;
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
