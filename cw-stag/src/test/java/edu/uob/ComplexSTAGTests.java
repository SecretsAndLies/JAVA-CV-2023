package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplexSTAGTests {

    private GameServer server;

    // tODO: https://pmd.github.io/ check with
    // todo: Go back through the spec and create comments for tests that you want. (eg: word choice, capitalization.)

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
//        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
        return server.handleCommand(command);
//                },
//                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testCutDownTwoWords() {
        String response;
        response = sendCommandToServer("simon: get, axe");
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("simon: inv.");
        assertTrue(response.contains("axe"));
        // note you do not have to get axe in order to use it if it's in the same room. Can't test this here need to write a new game file.
        response = sendCommandToServer("simon: goto-forest");
        response = sendCommandToServer("simon: down slice tree with axe");
        assertTrue(response.contains("A command must include only and only one action keyphrase."));
        response = sendCommandToServer("simon: cut down tree with axe");
        assertTrue(response.contains("You cut down the tree with the axe"));
        response = sendCommandToServer("simon: look");
        // log should now be in the level and tree should be gone.
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));

        setup();
        response = sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"));
        // todo: write a game file where you use an object in the same room as the subject - shoudlnt' ahve to be in inv to use..
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: slice it down tree with axe");
        assertTrue(response.contains("You cut down the tree with the axe"));
        response = sendCommandToServer("simon: look");
        // log should now be in the level and tree should be gone.
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));

    }

    @Test
    void testPlayingTheGame() {
        String response;
        response = sendCommandToServer("simon: look");
        response = sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: get coin");
        response = sendCommandToServer("simon: inventory");
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open door");
        assertTrue(response.contains("Can't execute this action."));
        response = sendCommandToServer("simon: open trapdoor");
//        System.out.println(response);
        response = sendCommandToServer("simon: get shovel");
        assertTrue(response.contains("I can't pick up shovel"));
        response = sendCommandToServer("simon: inventory");
        assertFalse(response.contains("shovel"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: pay elf");
//        System.out.println(response);
        response = sendCommandToServer("simon: get shovel");
        response = sendCommandToServer("simon: goto forest");
        assertTrue(response.contains("Can't access that location from here"));
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: inv");
//        System.out.println(response);
        response = sendCommandToServer("simon: chop down the tree with the axe");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("deep dark forest."));
        response = sendCommandToServer("simon: goto riverbank");
        System.out.println(response);
        response = sendCommandToServer("simon: bridge river with the log");
        System.out.println(response);
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get log");
        response = sendCommandToServer("simon: goto riverbank");

        response = sendCommandToServer("simon: bridge river with the log");
        System.out.println(response);
        response = sendCommandToServer("simon: look");
        System.out.println(response);
        response = sendCommandToServer("simon: get horn");
        System.out.println(response);
        response = sendCommandToServer("simon: look");
        System.out.println(response);
        response = sendCommandToServer("simon: inv");
       assertTrue(response.contains("horn"));

        response = sendCommandToServer("simon: goto clearing");
        assertTrue(response.contains("clearing in the woods"));
        response = sendCommandToServer("simon: Dig at the ground with the shovel.");
        assertTrue(response.contains("You dig into the soft ground and unearth a pot of gold"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("hole"));
        response = sendCommandToServer("simon: get gold");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("gold"));
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("gold"));
        response = sendCommandToServer("simon: get hole");
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("hole"));
        response = sendCommandToServer("simon: blow horn");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("lumberjack"));
    }

}
