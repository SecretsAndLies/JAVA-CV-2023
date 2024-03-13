package edu.uob;

import java.util.ArrayList;

public class OXOModel {

    private ArrayList<ArrayList<OXOPlayer>> cells;
    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public ArrayList<ArrayList<OXOPlayer>> getCells() {
        return cells;
    }

    public void setCells(ArrayList<ArrayList<OXOPlayer>> cells) {
        this.cells = cells;
    }

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new ArrayList<>(numberOfRows);
        for (int i = 0; i < numberOfRows; i++) {
            cells.add(new ArrayList<>(numberOfColumns));
            for (int j = 0; j < numberOfColumns; j++) {
                cells.get(i).add(null);
            }
        }
        this.winner=null;
        this.gameDrawn=false;
        players = new OXOPlayer[2];
    }

    public int getNumberOfPlayers() {
        return players.length;
    }

    public void addPlayer(OXOPlayer player) {
        int len = players.length;
        for (int i = 0; i < len; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
        // if you’re here, the arary wasn't big enough. copy it it into a larger one.
        OXOPlayer[] newPlayers = new OXOPlayer[len+1];
        System.arraycopy(players,0,newPlayers,0, len);
        players = newPlayers;
        addPlayer(player); // hack - since we need to run the top of this method again, we can just run this
        // recursively. Should never run more than once.
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players[number];
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public boolean isOutOfBounds(int row, int col){
        if(row<0 || row>this.getNumberOfRows()-1){
            return true;
        }
        if(col<0 || col>this.getNumberOfColumns()-1){
            return true;
        }
        return false;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public boolean valueInRow(int rowNum){
        for (int i = 0; i < getNumberOfColumns(); i++) {
            if(cells.get(rowNum).get(i)!=null){
                return true;
            }
        }
        return false;
    }

    public boolean valueInCol(int colNum){
        for (int i = 0; i < getNumberOfRows(); i++) {
            if(cells.get(i).get(colNum)!=null){
                return true;
            }
        }
        return false;
    }

    public boolean boardHasMove(){
        for (int i = 0; i < getNumberOfRows(); i++) {
            if(valueInRow(i)){
                return true;
            }
        }
        return false;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber,player);
    }
    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn(boolean isDrawn) {
        gameDrawn = isDrawn;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

}
