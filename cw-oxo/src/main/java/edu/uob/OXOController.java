package edu.uob;

import java.util.ArrayList;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        // a2
        if (gameModel.isGameDrawn()) {
            return;
        }
        if (gameModel.getWinner() != null) {
            return;
        }
        if (command.length() != 2) {
            throw new OXOMoveException("too long");
        }
        char rowChar = command.charAt(0);
        int row;
        if (Character.isUpperCase(rowChar)) {
            row = rowChar - 'A';
        } else if (Character.isLowerCase(rowChar)) {
            row = rowChar - 'a';
        } else {
            throw new OXOMoveException("non alphabetic first char");
        }
        if (row + 1 > gameModel.getNumberOfRows()) {
            throw new OXOMoveException("row doesn't exist");
        }

        int col = Character.getNumericValue(command.charAt(1)) - 1;
        if (col + 1 > gameModel.getNumberOfColumns()) {
            throw new OXOMoveException("col doesn't exist");
        }

        int currPlayerNum = gameModel.getCurrentPlayerNumber();
        gameModel.setCellOwner(row, col, gameModel.getPlayerByNumber(currPlayerNum));
        int newPlayerNum = (gameModel.getCurrentPlayerNumber() + 1) % gameModel.getNumberOfPlayers();
        gameModel.setCurrentPlayerNumber(newPlayerNum);

    }

    public boolean checkForWinner(int row, int col){
        // vertical
        if(checkVerticalWin(row, col)){
            return true;
        }

        if(checkHorizontalWin(row, col)){
            return true;
        }

        if(checkDiagonalWin(row, col)){
            return true;
        }

        return false;
    }

    private boolean checkHorizontalWin(int row, int col){
        return false;
    }

    private boolean checkDiagonalWin(int row, int col){
        return false;
    }

    private boolean checkVerticalWin(int row, int col){
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            
        }
        return false;
    }

    public void addRow() {
        gameModel.setGameDrawn(false);
        int rows = gameModel.getNumberOfRows();
        if (rows == 9) {
            return;
        }
        int cols = gameModel.getNumberOfColumns();
        gameModel.getCells().add(new ArrayList<>(cols));
        for (int i = 0; i < cols; i++) {
            // rows has now increased, so you can use it as the index of the last element.
            gameModel.getCells().get(rows).add(null);
        }
    }

    public void removeRow() {
        int rows = gameModel.getNumberOfRows();
        if (rows == 3) {
            return;
        }
        if (gameModel.valueInRow(rows - 1)) {
            return;
        }
        gameModel.getCells().remove(rows - 1);
    }

    public void addColumn() {
        gameModel.setGameDrawn(false);
        int cols = gameModel.getNumberOfColumns();
        if (cols == 9) {
            return;
        }
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            gameModel.getCells().get(i).add(null);
        }
    }



    public void removeColumn() {
        int cols = gameModel.getNumberOfColumns();
        if (cols == 3) {
            return;
        }
        if (gameModel.valueInCol(cols - 1)) {
            return;
        }
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            gameModel.getCells().get(i).remove(gameModel.getNumberOfColumns() - 1);
        }
    }

    public void increaseWinThreshold() {
        int win = gameModel.getWinThreshold();
        gameModel.setWinThreshold(win + 1);
    }

    public void decreaseWinThreshold() {
        int win = gameModel.getWinThreshold();
        gameModel.setWinThreshold(win - 1);
    }

    public void reset() {
        gameModel.reset();
    }
}
