package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class GameAction
{
    List<String> subjects;
    List<String> consumed;
    List<String> produced;
    String narration;

    public GameAction(List<String> subjects, List<String> consumed, List<String> produced, String narration) {
        this.subjects = new ArrayList<>(subjects);
        this.consumed = new ArrayList<>(consumed);
        this.produced = new ArrayList<>(produced);
        this.narration = narration;
    }

    public String toString(){
        return "Subjects: " + subjects.toString() + "\n" +
                "Consumed: " + consumed.toString() + "\n" +
                "Produced: " + produced.toString() + "\n" +
                "Narration: " + narration;
    }
}
