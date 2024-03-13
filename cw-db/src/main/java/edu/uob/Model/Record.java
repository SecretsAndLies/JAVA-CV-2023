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

    @Override
    public String toString() {
        return data.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\t")).concat("\n");
    }
}
