package edu.uob.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Record {
    List<String> data;

    public Record(Integer id, List<String> valueList) {
        data = new ArrayList<>();
        data.add(id.toString());
        data.addAll(valueList);
    }

    public String getByIndex(int index) {
        return data.get(index);
    }

    public ArrayList<String> copyData() {
        return new ArrayList<>(this.data);
    }

    public void addCol() {
        data.add("");
    }

    public void setColVal(int colIndex, String colValue) {
        data.set(colIndex, colValue);
    }

    public void removeCol(int i) {
        data.remove(i);
    }

    @Override
    public String toString() {
        return data.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\t")).concat("\n");
    }
}
