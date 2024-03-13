package edu.uob.Utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static boolean isReservedKeyword(String keyword){
        List<String> reservedKeywords = new ArrayList<>(
                List.of("USE", "CREATE", "DATABASE", "TABLE", "DROP",
                        "ALTER", "INSERT", "INTO", "VALUES", "SELECT", "FROM",
                        "WHERE", "UPDATE", "SET", "DELETE", "JOIN", "ON", "ADD",
                        "TRUE", "FALSE", "NULL", "AND", "OR", "LIKE"
                ));
        return reservedKeywords.contains(keyword.toUpperCase());
    }


}
