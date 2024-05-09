package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameAction {
    private final List<String> subjects;
    private final List<String> consumed;
    private final List<String> produced;
    private final String narration;

    public GameAction(List<String> subjects, List<String> consumed,
                      List<String> produced, String narration) {
        this.subjects = new ArrayList<>(subjects);
        this.consumed = new ArrayList<>(consumed);
        this.produced = new ArrayList<>(produced);
        this.narration = narration;
    }

    public boolean actionContainsAllSubjects(List<String> subjects) {
        return new HashSet<>(this.subjects).containsAll(subjects);
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public boolean deepEquals(GameAction other) {
        if (!this.subjects.equals(other.subjects)) {
            return false;
        }
        if (!this.consumed.equals(other.consumed)) {
            return false;
        }
        if (!this.produced.equals(other.produced)) {
            return false;
        }
        return this.narration.equals(other.narration);
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

    @Override
    public String toString() {
        return "Subjects: " + subjects + "\n" +
                "Consumed: " + consumed + "\n" +
                "Produced: " + produced + "\n" +
                "Narration: " + narration;
    }
}
