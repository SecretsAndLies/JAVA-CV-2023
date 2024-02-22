package edu.uob;

public class OXOPlayer {

    private char letter;

    public OXOPlayer(char playingLetter) {
        letter = playingLetter;
    }

    public char getPlayingLetter() {
        return letter;
    }

    public void setPlayingLetter(char letter) {
        this.letter = letter;
    }
}
