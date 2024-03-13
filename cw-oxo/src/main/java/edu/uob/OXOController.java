package edu.uob;

import java.util.ArrayList;

import static edu.uob.OXOMoveException.RowOrColumn.ROW;
import static java.lang.Character.isDigit;
import static java.lang.Character.toLowerCase;
import static edu.uob.OXOMoveException.RowOrColumn.COLUMN;
import static java.lang.Math.*;

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
        //Invalid Identifier Length: The entire identifier string is longer (or shorter) than the required two characters
        if (command.length() != 2) {
            throw new OXOMoveException.InvalidIdentifierLengthException(command.length());
        }

        //Invalid Identifier Character: The row character is not alphabetic or the column character is not numerical
        char rowChar = toLowerCase(command.charAt(0));
        int row;
        if (Character.isLowerCase(rowChar)) {
            row = rowChar - 'a';
        } else {
            throw new OXOMoveException.InvalidIdentifierCharacterException(ROW, rowChar);
        }
        char colChar = command.charAt(1);
        if(!isDigit(colChar)){
            throw new OXOMoveException.InvalidIdentifierCharacterException(COLUMN,colChar);
        }

        //Outside Range: The identifiers are valid, but they are out the range of the board size (i.e. too big or too small)
        if (row <0 || row + 1 > gameModel.getNumberOfRows()) {
            throw new OXOMoveException.OutsideCellRangeException(ROW,row);
        }

        int col = Character.getNumericValue(colChar - 1);

        if (col<0 || col + 1 > gameModel.getNumberOfColumns()) {
            throw new OXOMoveException.OutsideCellRangeException(COLUMN,col);
        }

        // Already Taken: The specified cell exists, but it has already been claimed by a player
        if(gameModel.getCellOwner(row,col)!=null){
            throw new OXOMoveException.CellAlreadyTakenException(row,col);
        }

        //
        int currPlayerNum = gameModel.getCurrentPlayerNumber();
        gameModel.setCellOwner(row, col, gameModel.getPlayerByNumber(currPlayerNum));
        if(checkForWinner(row,col)){
            gameModel.setWinner(gameModel.getPlayerByNumber(currPlayerNum));
            return;
        }
        if(checkForDraw()){
            gameModel.setGameDrawn(true);
        }
        int newPlayerNum = (currPlayerNum + 1) % gameModel.getNumberOfPlayers();
        gameModel.setCurrentPlayerNumber(newPlayerNum);

    }

    public boolean checkForDraw(){
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if(gameModel.getCellOwner(i,j)==null){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkForWinner(int row, int col) {
        // vertical
        if (checkVerticalWin(col)) {
            return true;
        }

        if (checkHorizontalWin(row)) {
            return true;
        }

        if (checkDiagonalWin(row, col)) {
            return true;
        }

        return false;
    }

    // todo duplicate of vertical.
    private boolean checkVerticalWin(int col) {
        int count = 0;
        int max = 0;
        char player = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()).getPlayingLetter();
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            OXOPlayer cell = gameModel.getCells().get(i).get(col);
            if(cell!=null){
                char letter = gameModel.getCells().get(i).get(col).getPlayingLetter();
                if(letter==player){
                    count++;
                }
                else{
                    count = 0;
                }
            }
            else{
                count = 0;
            }
            max = max(count,max);
        }
        return max==gameModel.getWinThreshold();
    }

    // todo this needs a refactor. Duplicative.
    private boolean checkDiagonalWin(int row, int col) {
        // left diagonal
        int i=row;
        int j=col;
        int count =0;
        char player = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()).getPlayingLetter();
        while (!gameModel.isOutOfBounds(i,j)){
            if(gameModel.getCellOwner(i,j)!=null){
                if(gameModel.getCellOwner(i,j).getPlayingLetter()==player){
                    count++;
                }
            }
            i--;
            j--;
        }
        if(count==gameModel.getWinThreshold()){
            return true;
        }
        i=row;
        j=col;
        count--; // avoids double counting.
        while (!gameModel.isOutOfBounds(i,j)){
            if(gameModel.getCellOwner(i,j)!=null){
                if(gameModel.getCellOwner(i,j).getPlayingLetter()==player){
                    count++;
                }
            }
            i++;
            j++;
        }
        if(count==gameModel.getWinThreshold()){
            return true;
        }

        // right diagonal
         i=row;
         j=col;
         count =0;
        while (!gameModel.isOutOfBounds(i,j)){
            if(gameModel.getCellOwner(i,j)!=null){
                if(gameModel.getCellOwner(i,j).getPlayingLetter()==player){
                    count++;
                }
            }
            i--;
            j++;
        }
        if(count==gameModel.getWinThreshold()){
            return true;
        }
        i=row;
        j=col;
        count--; // avoids double counting.
        while (!gameModel.isOutOfBounds(i,j)){
            if(gameModel.getCellOwner(i,j)!=null){
                if(gameModel.getCellOwner(i,j).getPlayingLetter()==player){
                    count++;
                }
            }
            i++;
            j--;
        }
        return count == gameModel.getWinThreshold();
    }

    private boolean checkHorizontalWin(int row) {
        int count = 0;
        int max = 0;
        char player = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()).getPlayingLetter();
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            OXOPlayer cell = gameModel.getCells().get(row).get(i);
            if(cell!=null){
                char letter = gameModel.getCells().get(row).get(i).getPlayingLetter();
                if(letter==player){
                    count++;
                }
                else{
                    count = 0;
                }
            }
            else{
                count = 0;
            }
            max = max(count,max);
        }
        return max==gameModel.getWinThreshold();
    }

    public void addRow() {
        if(isGameOver()){
            return;
        }
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

    private boolean isGameOver(){
        return gameModel.isGameDrawn() || gameModel.getWinner() != null;
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
        if(isGameOver()){
            return;
        }
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
        if(win>=min(gameModel.getNumberOfRows(), gameModel.getNumberOfColumns())){
            return;
        }
        if(gameModel.getWinner()!=null){
            return;
        }
        gameModel.setWinThreshold(win + 1);
    }

    public void decreaseWinThreshold() {
        int win = gameModel.getWinThreshold();
        if(win<=3){
            return;
        }
        if(gameModel.boardHasMove()){
            return;
        }
        gameModel.setWinThreshold(win - 1);
    }


    public void reset() {
        for (int i = 0; i < gameModel.getCells().size(); i++) {
            for (int j = 0; j < gameModel.getCells().get(0).size(); j++) {
                gameModel.setCellOwner(i, j, null);
            }
        }
        gameModel.setWinner(null);
        gameModel.setGameDrawn(false);
    }
}
