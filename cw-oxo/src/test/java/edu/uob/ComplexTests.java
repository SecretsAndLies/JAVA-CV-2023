package edu.uob;

import edu.uob.OXOMoveException.CellAlreadyTakenException;
import edu.uob.OXOMoveException.InvalidIdentifierCharacterException;
import edu.uob.OXOMoveException.InvalidIdentifierLengthException;
import edu.uob.OXOMoveException.OutsideCellRangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ComplexTests {
  private OXOModel model;
  private OXOController controller;
  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
      // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
      // Note: this is ugly code and includes syntax that you haven't encountered yet
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }
  @Test
  void test3PlayerGame() throws OXOMoveException {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    model.addPlayer(new OXOPlayer('Y'));
    controller = new OXOController(model);
    // Find out which player is going to make the first move
    OXOPlayer xPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1");
    OXOPlayer oPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("b1");
    OXOPlayer yPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("c1");
    assertEquals(yPlayer, controller.gameModel.getCellOwner(2, 0));

    sendCommandToController("a2");
    sendCommandToController("b2");
    sendCommandToController("c2");
    sendCommandToController("b3");
    sendCommandToController("a3");
    sendCommandToController("c3");
    assertEquals(yPlayer, model.getWinner());
  }


  // todo test vertical, diagonal, horizontal victory.
  // todo test weird boards ie long and skinny etc.
  // todo blank squares in the middle etc.
  // todo test draw

}
