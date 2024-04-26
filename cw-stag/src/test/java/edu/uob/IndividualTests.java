package edu.uob;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.nio.file.Paths;

public class IndividualTests {
    // ensure that all items are collectable and all furniture isn't
    // try to collect a character

    private GameServer server;

    // todo: before submission: pmd_check | grep -vE "Tests"
    // add the timeout back in.
    // todo - rewrite the string returns to make prettier
    // todo: rewwork your code quality, cyclomatic complexity etc.
    // todo: test with the command line on the lab machine.

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


}
