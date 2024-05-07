package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplexSTAGTests {

    private GameServer server;

    // todo: before submission: pmd_check | grep -vE "Tests"
    // add the timeout back in.
    // todo: rewwork your code quality, cyclomatic complexity etc.
    // todo: test with the command line on the lab machine.

    // todo do more extraneuo

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities2.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions2.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
//        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
        return server.handleCommand(command);
//                },
//                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // you cannot consume a locaiton that's not connected to your current location.
    // look look

    @Test
    void testCompositeCommands2() {
        String response;
        response = sendCommandToServer("simon: summon potion");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: open and bash trapdoor");
        assertTrue(response.contains("Multiple actions are available, can you be more specific"));
    }

    @Test
    void testDoubleWordCommands() {
        String response;
        response = sendCommandToServer("simon: look look");
        assertTrue(response.contains("log cabin in the woods"));
        response = sendCommandToServer("simon: summon potion");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: open and unlock trapdoor");
        assertTrue(response.contains("You unlock the door and see steps leading down into a cellar"));
        // todo test empty subjects trying to do things without right resources etc.

        // artifacts
        response = sendCommandToServer("simon: get potion potion");
        assertTrue(response.contains("potion added to your inventory"));

        response = sendCommandToServer("simon: drop potion potion");
        assertTrue(response.contains("dropped on the ground"));

        response = sendCommandToServer("simon: get trapdoor trapdoor");
        assertTrue(response.contains("Can't understand this command"));

        // locations
        response = sendCommandToServer("simon: goto forest forest");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("A deep dark forest"));

        response = sendCommandToServer("simon: kiss axe axe in cabin");
        System.out.println(response); // should work because the word is duplicated but same.

        response = sendCommandToServer("simon: kiss axe potion in cabin");
        assertTrue(response.contains("I can't do that."));


        // THIS ISN'T TESTED
//        response = sendCommandToServer("simon: look get");
//        System.out.println(response);  // this also should work because (not testing it though)


    }

    @Test
    void testInvalidPlayerName() {
        String response;
        response = sendCommandToServer("simon!: goto forest");
        assertTrue(response.contains("Player name is invalid."));
        // todo you could test other stuff here.
        response = sendCommandToServer("simon's: goto forest");
        assertTrue(response.contains("deep dark forest"));
        response = sendCommandToServer("bryan-s: goto forest");
        assertTrue(response.contains("deep dark forest"));
    }

    @Test
    void testCoLocation() {
        String response;

        response = sendCommandToServer("simon: kiss the axe");
        // ambiguious
        assertTrue(response.contains("Multiple actions are available, can you be more specific"));
        response = sendCommandToServer("simon: kiss the axe in the cabin");
        assertTrue(response.contains("You kiss the axe in the cabin and it costs health."));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: kiss the axe");
        assertTrue(response.contains("You kiss the axe outside the cabin and gain some health."));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
    }

    @Test
    void testThatAllSubjectsAreAvailableBeforeActionIsDone() {
        String response;
        response = sendCommandToServer("simon: loOk");
        response = sendCommandToServer("simon:  with ceLebrate, trapdoor");
        assertTrue(response.contains("can't do that"));
        response = sendCommandToServer("simon: goTo Forest");
        response = sendCommandToServer("simon: celebrate with key");
        assertTrue(response.contains("can't do that"));
        response = sendCommandToServer("simon: goto Riverbank");
        response = sendCommandToServer("simon: celebrAte with kEy");
        assertTrue(response.contains("can't do that"));
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: summon potion");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: inventory");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: trapdoor celebrate key potion");
        assertTrue(response.contains("log is produced"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("log"));
    }


    @Test
    void testLockDoorTwice() {
        String response;
        response = sendCommandToServer("simon: goto");
        assertTrue(response.contains("Can't understand this command"));
        response = sendCommandToServer("simon: goto cellar cabin");
        assertTrue(response.contains("Can't understand this command"));
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("Can't access that location from here"));
        response = sendCommandToServer("simon: lock trapdoor");
        assertTrue(response.contains("You've locked the trapdoor, how will you get it down now!"));
        response = sendCommandToServer("simon: lock trapdoor");
        assertTrue(response.contains("You've locked the trapdoor, how will you get it down now!"));
        response = sendCommandToServer("simon: get key");
        assertTrue(response.contains("I can't pick up key"));
        response = sendCommandToServer("simon: summon with potion");
        assertTrue(response.contains("You've used the magic potion to summoned the key from the forest"));
        response = sendCommandToServer("simon: open trapdoor");
        assertTrue(response.contains("see steps leading down into a cellar"));
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("A dusty cellar"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("A dusty cellar"));
        response = sendCommandToServer("simon: goto cabin");
        assertTrue(response.contains("A log cabin in the woods"));
        response = sendCommandToServer("simon: lock trapdoor");
        assertTrue(response.contains("You've locked the trapdoor, how will you get it down now"));
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("Can't access that location from here."));
    }

    // Note that it is NOT possible to perform an action where a subject,
    // or a consumed or produced entity is currently in another player's inventory.
    @Test
    void testNotPossiblePerformActionInAnotherInventory() {
        String response;
        response = sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("potion"));
        response = sendCommandToServer("james: look");
        assertTrue(response.contains("axe"));
        response = sendCommandToServer("james: get potion");
        assertTrue(response.contains("can't pick up"));
        response = sendCommandToServer("james: po summon with axe");
        // this action should be impossible, because you cannot produce the potion, since it's in
        // simon's inventory
        assertTrue(response.contains("can't do that"));

        // Attempt to run an action that consumes an item in another players inventory. Should fail
        response = sendCommandToServer("james: disappear axe");
        assertTrue(response.contains("I can't do that"));
    }

    @Test
    void testProducingItemInSameLocation() {
        String response;
        response = sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: goto forest");
        assertTrue(response.contains("A deep dark forest"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: summon with potion");
        assertTrue(response.contains("used the magic potion"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: summon with potion");
        assertTrue(response.contains("used the magic potion"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("1"));
    }

    @Test
    void testProduceAndConsumeCharacter() {
        String response;
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("dog"));
        response = sendCommandToServer("simon: call doggo");
        assertTrue(response.contains("I can't do that"));
        response = sendCommandToServer("simon: call axe");
        assertTrue(response.contains("Your dog is here"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("dog"));
        response = sendCommandToServer("simon: eat dog");
        assertTrue(response.contains("You ate your"));
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("dog"));
        response = sendCommandToServer("simon: call axe");
        assertTrue(response.contains("Your dog is here"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("dog"));
    }

    @Test
    void testDecoratedCommands() {
        String response;
        response = sendCommandToServer("simon: goto river");
        assertTrue(response.contains("Can't understand this command"));

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
        response = sendCommandToServer("simon: se look");
        assertTrue(response.contains("axe"));

        response = sendCommandToServer("bryan: get axe!");
        assertTrue(response.contains("axe added to your inventory."));
        response = sendCommandToServer("bryan: check your inventory");
        assertTrue(response.contains("axe"));

        response = sendCommandToServer("simon: get axe");
        assertTrue(response.contains("I can't pick up axe"));
        response = sendCommandToServer("bryan: goto forest!");
        response = sendCommandToServer("simon: goto the forest");
        response = sendCommandToServer("simon: chop tree");  // fails, bryan has the axe
        assertTrue(response.contains("can't"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("deep dark forest"));
        assertTrue(response.contains("tree"));
        response = sendCommandToServer("bryan: drop ax,e"); // punctuation is intrepted as spaces, so this fails.
        assertTrue(response.contains("Can't understand"));
        response = sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: chop tree drop axe"); // should fail as composite.
        assertTrue(response.contains("Can't understand"));
        response = sendCommandToServer("simon: check your health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: look get");
        assertTrue(response.contains("found more command words than expected"));
    }


    @Test
    void testPlayerIsResetToStart() {
        String response;
        response = sendCommandToServer("james: get potion");
        response = sendCommandToServer("james: summon with potion");
        assertTrue(response.contains("You've used the magic potion to summoned the key from the forest"));
        response = sendCommandToServer("james: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("james: open trapdoor");
        assertTrue(response.contains("leading down into a cellar"));
        response = sendCommandToServer("james: goto cellar");
        assertTrue(response.contains("dusty cellar"));
        response = sendCommandToServer("bryan: goto cellar");
        assertTrue(response.contains("dusty cellar"));
        assertTrue(response.contains("james"));
        response = sendCommandToServer("james: hit elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("james: hit elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("james: look");
        assertTrue(response.contains("log cabin"));
        response = sendCommandToServer("bryan: look");
        assertTrue(response.contains("dusty cellar"));
        assertTrue(response.contains("potion"));
        assertFalse(response.contains("james"));


    }

    @Test
    void testCantGetFurniture() {
        String response;
        response = sendCommandToServer("james: use potion to magically create");
        assertTrue(response.contains("You used the potion to magically create furnitureItem"));
        response = sendCommandToServer("james: get furnitureItem");
        assertTrue(response.contains("Can't understand this command"));
        response = sendCommandToServer("james: look");
        assertTrue(response.contains("furnitureitem"));
        response = sendCommandToServer("james: use potion to magically destroy");
        assertTrue(response.contains("You used the potion to magically destroy furnitureItem"));
        response = sendCommandToServer("james: look");
        assertFalse(response.contains("furnitureitem"));
        response = sendCommandToServer("james: use potion to magically create");
        assertTrue(response.contains("You used the potion to magically create furnitureItem"));
        response = sendCommandToServer("james: look");
        assertTrue(response.contains("furnitureitem"));
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
        response = sendCommandToServer("simon: health elf");
        assertTrue(response.contains("Can't understand this command"));
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
    void testCompositeCommands() {
        String response;
        response = sendCommandToServer("james: get axe look");
        assertTrue(response.contains("found more command words than expected"));
        response = sendCommandToServer("james: look get axe");
        assertTrue(response.contains("found more command words than expected"));
        response = sendCommandToServer("james: health get axe");
        assertTrue(response.contains("found more command words than expected"));

        response = sendCommandToServer("james: open summon potion");
        assertTrue(response.contains("used the magic potion to summoned"));
        response = sendCommandToServer("james: health");
        assertTrue(response.contains("2"));
    }

    @Test
    void testAmbiguousCommands() {
        String response;
        response = sendCommandToServer("james: pour potion");
        assertTrue(response.contains("Multiple actions are available, can you be more specific"));
        response = sendCommandToServer("james: look");
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("coin"));
        response = sendCommandToServer("james: pour potion to destroy the coin");
        assertTrue(response.contains("You pour the potion onto the coin and it disappears"));
        response = sendCommandToServer("james: look");
        assertTrue(response.contains("axe"));
        assertFalse(response.contains("coin"));
        response = sendCommandToServer("james: pour potion");
        assertTrue(response.contains("You pour the potion onto the axe and it disappears"));
        response = sendCommandToServer("james: look");
        assertFalse(response.contains("axe"));
        assertFalse(response.contains("coin"));
    }


    @Test
    void testSummon() {
        String response;
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("key"));
        response = sendCommandToServer("simon: summon with the potion"); // summons the key
        assertTrue(response.contains("You've used the magic potion to summoned the key from the forest"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"));
        response = sendCommandToServer("simon: open trapdoor");
        assertTrue(response.contains("unlock the door and see steps leading down into a cellar"));
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
        assertTrue(response.contains("deep dark forest"));
        response = sendCommandToServer("simon: repair tree");
        assertTrue(response.contains("I can't do that."));
        response = sendCommandToServer("simon: repair log");
        assertTrue(response.contains("I can't do that."));
        response = sendCommandToServer("simon: down slice tree with axe");
        assertTrue(response.contains("I can't do that."));
        response = sendCommandToServer("simon: cut down tree with axe");
        assertTrue(response.contains("You cut down the tree with the axe"));
        response = sendCommandToServer("simon: look");
        // log should now be in the level and tree should be gone.
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));
        response = sendCommandToServer("simon: repair log");
        assertTrue(response.contains("You repaired the log back into a tree."));
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("log"));
        assertTrue(response.contains("tree"));
        response = sendCommandToServer("simon: cut that thing down with your trusty axe");
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
    void testCantSeeOtherCharacter() {
        String response;
        response = sendCommandToServer("simon: axe get");
        response = sendCommandToServer("bryan: look");
        assertTrue(response.contains("simon"));
        assertFalse(response.contains("bryan"));
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("simon"));
        assertTrue(response.contains("bryan"));
        response = sendCommandToServer("simon: get Bryan");
        assertTrue(response.contains("Can't understand"));
        response = sendCommandToServer("simon: goto forest");
        assertTrue(response.contains("You are in A deep dark forest"));
        response = sendCommandToServer("simon: look"); // Forest simon can't see anyone.
        assertFalse(response.contains("simon"));
        assertFalse(response.contains("bryan"));
        response = sendCommandToServer("bryan: look"); // cabin bryan can't see anyone.
        assertFalse(response.contains("simon"));
        assertFalse(response.contains("bryan"));
        response = sendCommandToServer("bryan: goto forest");
        assertTrue(response.contains("You are in A deep dark forest"));
        response = sendCommandToServer("bryan: look");
        assertTrue(response.contains("simon"));
        assertFalse(response.contains("bryan"));
    }

    @Test
    void testPlayingTheGame() {
        String response;
        response = sendCommandToServer("simon: axe get");
        assertTrue(response.contains("axe added to your inventory"));
        response = sendCommandToServer("simon: look");
        response = sendCommandToServer("simon: drop potion");
        assertTrue(response.contains("potion isn't in your inventory"));
        response = sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: get coin");
        response = sendCommandToServer("simon: inventory");
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: bash trapdoor");
        assertTrue(response.contains("You bash at the trapdoor with your hand and lose some health"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: open door");
        assertTrue(response.contains("I can't do that."));
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
        assertTrue(response.contains("shovel"));
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
        assertTrue(response.contains("Multiple actions are available"));
        response = sendCommandToServer("simon: bash trapdoor with shovel");
        assertTrue(response.contains("You manage to bash down the door with the shovel"));
        // repeating this action should give the same answer but not generate new objects.
        response = sendCommandToServer("simon: bash trapdoor with shovel");
        assertTrue(response.contains("You manage to bash down the door with the shovel"));
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
        response = sendCommandToServer("simon: magically consume thing");
        assertTrue(response.contains("I can't do that"));
        response = sendCommandToServer("simon: magically consume thing key");
        assertTrue(response.contains("I can't do that"));
        // consume an object that's not in your location.
        response = sendCommandToServer("simon: magically consume thing using the magic shovel");
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
        assertTrue(response.contains("deep dark forest"));
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
        response = sendCommandToServer("simon: goto riverbank");
        assertTrue(response.contains("Can't access"));
        // produce a location that already exists
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("woodenplanks"));
        response = sendCommandToServer("simon: assemble bridge with woodenPlanks");
        assertTrue(response.contains("turned some of the planks into a bridge"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("pile of wooden"));
        assertTrue(response.contains("clearing in the woods"));
        response = sendCommandToServer("simon: assemble bridge with woodenPlanks");
        response = sendCommandToServer("simon: goto riverbank");
        assertTrue(response.contains("grassy riverbank"));
        response = sendCommandToServer("simon: goto clearing");
        response = sendCommandToServer("simon: look");
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
        assertTrue(response.contains("lumberjack")); // tests that characters can be created.
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
        response = sendCommandToServer("simon: murder lUmberjack"); // tests that characters can be consumed.
        assertTrue(response.contains("You murdered the lumberjack. Oh, the humanity"));
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("lumberjack"));
    }


}
