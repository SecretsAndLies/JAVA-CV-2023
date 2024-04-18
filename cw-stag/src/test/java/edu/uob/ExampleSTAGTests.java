package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExampleSTAGTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
//        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
        return server.handleCommand(command);
//                },
//                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
    @Test
    void testLook() {
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    // Test that we can pick something up and that it appears in our inventory
    @Test
    void testGet() {
        String response;
        response = sendCommandToServer("simon: inv");
        sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
        response = sendCommandToServer("simon: drop potion");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("empty"));
        assertFalse(response.contains("potion"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("potion"));
        response = sendCommandToServer("simon: drop potion");
        assertTrue(response.contains("potion isn't in your inventory."));

    }

    // Test that we can goto a different location (we won't get very far if we can't move around the game !)
    @Test
    void testGoto() {
        String response;
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("Can't access"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cabin"));
        response = sendCommandToServer("simon: goto forest");
        assertTrue(response.contains("forest"));
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
    }

    @Test
    void testMultiplePlayers() {
        String response;
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("see characters"));
        response = sendCommandToServer("bryan: look");
        assertTrue(response.contains("see characters"));
        assertTrue(response.contains("simon"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("bryan"));
        assertTrue(response.contains("see characters"));
    }

    @Test
    void testCutDown(){
        String response;
        response = sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"));
        // note you do not have to get axe in order to use it if it's in the same room. Can't test this here need to write a new game file.
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: cutdown tree with axe");
        assertTrue(response.contains("You cut down the tree with the axe"));
        response = sendCommandToServer("simon: look");
        // log should now be in the level and tree should be gone.
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));
    }

    @Test
    void testOpen(){
        String response;
//        response = sendCommandToServer("simon: open trapdoor");
//        // todo: this isn't a great error. Ideally you'd be like "You need a key to open the trapdoor."
//        assertTrue(response.contains("I can't do that."));
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: goto cabin");
        // todo: locations doesn't work yet.
        response = sendCommandToServer("simon: open trapdoor");
        assertTrue(response.contains("You unlock the trapdoor and see steps leading down into a cellar"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"));
        assertFalse(response.contains("cabin"));

    }

    @Test
    void testDrinkandHealth(){
        String response;
        response = sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: chop tree");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: attack elf");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: drink potion");
        assertTrue(response.contains("You drink the potion and your health improves"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: attack elf");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: attack elf");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("1"));
        response = sendCommandToServer("simon: attack elf");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cabin"));
        assertFalse(response.contains("cellar"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"),"Expecting a dropped axe but found "+response);
        // todo: test that continuing to attack results in the player being placed at the start.
        // todo: test that the other player isn't affected.

    }

// todo: test a file with a two word keyphrase (eg: cut down instead of cutdown)

}
