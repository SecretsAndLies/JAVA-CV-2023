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
    void testCutDown() {
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
    void testOpen() {
        String response;
        response = sendCommandToServer("simon: open trapdoor");
        assertTrue(response.contains("I can't do that."));
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open trapdoor");
        assertTrue(response.contains("You unlock the trapdoor and see steps leading down into a cellar"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"));

    }

    @Test
    void testDrinkandHealth() {
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
    void testChop() {
        String response;
        response = sendCommandToServer("james: get axe");
        response = sendCommandToServer("james: goto forest");
        response = sendCommandToServer("james: chop tree");
        assertTrue(response.contains("You cut down the tree with the axe"));
        response = sendCommandToServer("simon: look");
        // log should now be in the level and tree should be gone.
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));
    }

    @Test
    void testCut() {
        String response;
        response = sendCommandToServer("james: get axe");
        response = sendCommandToServer("james: goto forest");
        response = sendCommandToServer("james: cut tree");
        assertTrue(response.contains("You cut down the tree with the axe"));
        response = sendCommandToServer("simon: look");
        // log should now be in the level and tree should be gone.
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));
    }

    @Test
    void testCutDown2() {
        String response;
        response = sendCommandToServer("james: get axe");
        response = sendCommandToServer("james: goto forest");
        response = sendCommandToServer("james: cutdown tree");
        assertTrue(response.contains("You cut down the tree with the axe"));
        response = sendCommandToServer("simon: look");
        // log should now be in the level and tree should be gone.
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));
    }

    @Test
    void testOpen2() {
        String response;
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open door with key");
        assertTrue(response.contains("You unlock the trapdoor and see steps leading down into a cellar"));

    }

    @Test
    void testUnlock() {
        String response;
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: with your key unlock the door");
        assertTrue(response.contains("You unlock the trapdoor and see steps leading down into a cellar"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"));

    }

    @Test
    void testUnlock2() {
        String response;
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: unlock with key");
        assertTrue(response.contains("You unlock the trapdoor and see steps leading down into a cellar"));
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"));

    }

    @Test
    void testWeirdSpacingAndCapitilization() {
        String response;
        response = sendCommandToServer("james may: gET Axe");
        assertTrue(response.contains("axe added to your inventory."));
        response = sendCommandToServer("james may: inv");
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("james may: goto forest");
        assertTrue(response.contains("dark forest"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: goto FOREST");
        assertTrue(response.contains("dark forest"));
        response = sendCommandToServer("simon: get axe");
        assertTrue(response.contains("I can't pick up axe"));
        response = sendCommandToServer("james may: drop axe");
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("ground"));
        response = sendCommandToServer("james may: look");
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("james may: inv");
        assertFalse(response.contains("axe"));
        response = sendCommandToServer("simon: get axe");
        assertTrue(response.contains("axe added to your inventory"));
        // composite command failure.
        response = sendCommandToServer("simon: get key and open door");
        assertTrue(response.contains("Can't understand this command"));
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("key"));
        response = sendCommandToServer("simon: get key");
        assertTrue(response.contains("key added to your inventory"));
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: goto cabin");
        assertTrue(response.contains("log cabin"));
        response = sendCommandToServer("simon: get potion");
        assertTrue(response.contains("potion added to your inventory"));
        response = sendCommandToServer("simon: open traPdoor");
        assertTrue(response.contains("You unlock the trapdoor and see steps leading down into a cellar"));
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("dusty cellar"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: attack eLF");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: drink POTION axe");
        assertTrue(response.contains("I can't do that."));
        response = sendCommandToServer("simon: drink axe POTION");
        assertTrue(response.contains("I can't do that."));

        response = sendCommandToServer("simon: drink pOTION");
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
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cabin"));
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("dusty cellar"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"), "Expecting a dropped axe but found " + response);
        response = sendCommandToServer("james may: look");
        assertTrue(response.contains("forest"));
        response = sendCommandToServer("simon: goto      cabin");
        assertTrue(response.contains("cabin"));

    }
}
