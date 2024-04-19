package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameAction
{
    private List<String> subjects;
    private List<String> consumed;
    private List<String> produced;
    private String narration;

    public GameAction(List<String> subjects, List<String> consumed, List<String> produced, String narration) {
        this.subjects = new ArrayList<>(subjects);
        this.consumed = new ArrayList<>(consumed);
        this.produced = new ArrayList<>(produced);
        this.narration = narration;
    }

    public boolean actionContainsAllSubjects(ArrayList<String> subjects){
        return new HashSet<>(this.subjects).containsAll(subjects);
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public List<String> getConsumed() {
        return consumed;
    }

    public List<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }

    public String toString(){
        return "Subjects: " + subjects.toString() + "\n" +
                "Consumed: " + consumed.toString() + "\n" +
                "Produced: " + produced.toString() + "\n" +
                "Narration: " + narration;
    }
}
