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
    void testDecoratedCommands() {
        String response;
        response = sendCommandToServer("simon: look at the room");
        assertTrue(response.contains("cabin"));
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("simon: get axe look");
        assertTrue(response.contains("found more command words than expected"));
        assertFalse(response.contains("axe"));
        response = sendCommandToServer("simon: get axe potion");
        assertTrue(response.contains("Can't understand this command"));
        response = sendCommandToServer("simon: get the axe");
        assertTrue(response.contains("axe added to your inventory"));
        response = sendCommandToServer("simon: inventory");
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("simon: drop the axe");
        response = sendCommandToServer("simon: inventory");
        assertFalse(response.contains("axe"));
        assertTrue(response.contains("empty"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: goto the forest");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("deep dark forest"));
        response = sendCommandToServer("simon: chop tree drop axe"); // should fail.
        assertTrue(response.contains("Can't understand"));
        response = sendCommandToServer("simon: check your health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: look get");
        assertTrue(response.contains("found more command words than expected"));
    }

    @Test
    void testHealth() {
        String response;
        response = sendCommandToServer("james: goto forest");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("elf"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: attack elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: drink potion");
        assertTrue(response.contains("You drink the potion and your health improves"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: hit elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));

        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: fight elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("1"));
        response = sendCommandToServer("simon: attack elf");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cabin"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"), "Expecting a dropped axe but found " + response);
        response = sendCommandToServer("james: look");
        assertTrue(response.contains("forest"));
    }

    @Test
    void testCutDownTwoWords() {
        String response;
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("Can't access that location from here."));
        response = sendCommandToServer("simon: drop axe");
        assertTrue(response.contains("axe isn't in your inventory"));
        response = sendCommandToServer("simon: get, axe");
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("simon: inventory axe");
        assertTrue(response.contains("Can't understand this command"));
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
        response = sendCommandToServer("simon: axe get");
        assertTrue(response.contains("axe added to your inventory"));
        response = sendCommandToServer("simon: look");
        response = sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: get coin");
        response = sendCommandToServer("simon: inventory");
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open door");
        assertTrue(response.contains("Can't execute this action."));
        response = sendCommandToServer("simon: open trapdoor");
        assertTrue(response.contains("You unlock the door and see steps leading down into a cellar"));
        response = sendCommandToServer("simon: get shovel");
        assertTrue(response.contains("I can't pick up shovel"));
        response = sendCommandToServer("simon: inventory");
        assertFalse(response.contains("shovel"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        response = sendCommandToServer("simon: pay elf");
//        System.out.println(response);
        response = sendCommandToServer("simon: get shovel");
        response = sendCommandToServer("simon: goto forest");
        assertTrue(response.contains("Can't access that location from here"));
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: get trapdoor");
        assertTrue(response.contains("Can't understand this command"));
        response = sendCommandToServer("simon: inventory");
        assertFalse(response.contains("trapdoor"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("trapdoor"));
        response = sendCommandToServer("simon: lock trapdoor");
        assertTrue(response.contains("how will you get it down now"));
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("cellar"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("A dusty cellar"));
        assertTrue(response.contains("log cabin in the woods"));
        response = sendCommandToServer("simon: bash trapdoor");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: inventory");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("A dusty cellar"));
        assertFalse(response.contains("log cabin in the woods"));
        response = sendCommandToServer("simon: drop key");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: goto cabin");
        assertTrue(response.contains("log cabin in the woods"));
        // needs a subject,
        response = sendCommandToServer("simon: magically consume key");
        assertTrue(response.contains("Can't execute this action"));
        // consume an object that's not in your location.
        response = sendCommandToServer("simon: magically consume key using the magic shovel");
        assertTrue(response.contains("magically has disappeared"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("key"));
        response = sendCommandToServer("simon: goto cabin");
        assertTrue(response.contains("log cabin in the woods"));
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: inv");
//        System.out.println(response);
        response = sendCommandToServer("simon: chop down the tree with the axe");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("deep dark forest."));
        response = sendCommandToServer("simon: goto riverbank");
        assertTrue(response.contains("grassy riverbank"));
        response = sendCommandToServer("simon: bridge river with the log");
        assertTrue(response.contains("can't do that"));
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get log");
        response = sendCommandToServer("simon: goto riverbank");

        response = sendCommandToServer("simon: bridge river with the log");
        assertTrue(response.contains("can now reach the other side"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("clearing"));
        response = sendCommandToServer("simon: get horn");
        assertTrue(response.contains("horn added to your inventory"));
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("horn"));
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("horn"));

        response = sendCommandToServer("simon: goto clearing");
        assertTrue(response.contains("clearing in the woods"));
        response = sendCommandToServer("simon: drop shovel");
        response = sendCommandToServer("simon: inventory");
        assertFalse(response.contains("shovel"));
        // dropped items are still usable.
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
        response = sendCommandToServer("simon: talk horn");
        assertTrue(response.contains("woah dude, nice horn"));
        response = sendCommandToServer("simon: talk lumberjack");
        assertTrue(response.contains("Multiple actions are available, can you be more specific"));
        response = sendCommandToServer("simon: talk to the lumberjack holding the Gold");
        assertTrue(response.contains("love the gold"));
        response = sendCommandToServer("simon: talk lumberjack horn gold");
        assertTrue(response.contains("I can't do that."));
        response = sendCommandToServer("simon: talk lumberjack horn look");
        assertTrue(response.contains("Can't understand this command"));
    }

}
