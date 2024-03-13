package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExampleControllerTests {
  private OXOModel model;
  private OXOController controller;

  // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
  // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
      // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
      // Note: this is ugly code and includes syntax that you haven't encountered yet
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  // Test simple move taking and cell claiming functionality
  @Test
  void testBasicMoveTaking() throws OXOMoveException {
    // Find out which player is going to make the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a move
    sendCommandToController("a1");
    // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
  }

  // Test out basic win detection
  @Test
  void testBasicWin() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  // Example of how to test for the throwing of exceptions
  @Test
  void testInvalidIdentifierException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
    // The next line is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for blank command";
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController(""), failedTestComment);

  }

  @Test
  void testTooLongIdentifier() throws OXOMoveException{
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `a0`";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("a0"), failedTestComment);

    failedTestComment = "Controller failed to throw an OutsideCellRangeException for command d1";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("d1"), failedTestComment);

    failedTestComment = "Controller failed to throw an OutsideCellRangeException for command b4";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("b4"), failedTestComment);

  }

  @Test
  void testInvalidCharacter() throws OXOMoveException {
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `$0`";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("$0"), failedTestComment);

    failedTestComment = "Controller failed to throw an OutsideCellRangeException for command 11";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("11"), failedTestComment);

    failedTestComment = "Controller failed to throw an OutsideCellRangeException for command aa";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("aa"), failedTestComment);
  }

  @Test
  void testAlreadyTaken() throws OXOMoveException{
    sendCommandToController("A1");
    String failedTestComment = "Controller failed to throw an CellAlreadyTakenException for command `A1`";
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("A1"), failedTestComment);

  }

  @Test
  void testWinThreshold() {
    int oldWin = model.getWinThreshold();
    controller.decreaseWinThreshold();
    //The minimum win threshold should be 3 (a game with a lower threshold is going to be pretty pointless !).
   // so it shouldn’t have changed.
    assertEquals(oldWin,model.getWinThreshold());

    // The maximum win threshold should be the current size of the board (which ever is smaller: width or height).
    controller.increaseWinThreshold();
    assertEquals(oldWin,model.getWinThreshold());
    // adding just one row won't work.
    controller.addRow();
    controller.increaseWinThreshold();
    assertEquals(oldWin,model.getWinThreshold());
    // but if we add a column too it will work.
    controller.addColumn();
    controller.increaseWinThreshold();
    assertEquals(oldWin+1,model.getWinThreshold());

    //users can decrease win before moves are played).
    controller.decreaseWinThreshold();
    assertEquals(oldWin,model.getWinThreshold());

    // but not after.
    controller.increaseWinThreshold();
    sendCommandToController("A1");
    controller.decreaseWinThreshold();
    assertEquals(oldWin+1,model.getWinThreshold());

    // users can increase the threshold during the game
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    assertEquals(oldWin+6,model.getWinThreshold());
    // cannot increase past 9.
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    assertEquals(oldWin+6,model.getWinThreshold());
    assertEquals(9, model.getNumberOfRows());
    assertEquals(9, model.getNumberOfColumns());

    // decrease back to win of 3.
    model.setWinThreshold(4);
    controller.removeColumn();
    controller.removeRow();
    controller.removeColumn();
    controller.removeRow();
    controller.removeColumn();
    controller.removeRow();
    controller.removeColumn();
    controller.removeRow();
    controller.removeColumn();
    controller.removeRow();
    assertEquals(4, model.getNumberOfRows());
    assertEquals(4, model.getNumberOfColumns());

    // create a win diagonally
    // a1 has already been played.
    sendCommandToController("B1");
    sendCommandToController("B2");
    sendCommandToController("C1");
    sendCommandToController("C3");
    sendCommandToController("D1");
    sendCommandToController("D4");

    assertNotNull(model.getWinner());
    // The win threshold should NOT be changed after a win - gameplay should cease when someone has won !
    controller.increaseWinThreshold();
    assertEquals(4,model.getWinThreshold());
    // you should NOT alter the win threshold when the game is reset
    controller.reset();
    assertEquals(4,model.getWinThreshold());


  }
}
