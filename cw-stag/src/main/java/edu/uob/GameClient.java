package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
* This is the sample client for you to connect to your game server.
*
* <p>Input are taken from stdin and output goes to stdout.
*/
public final class GameClient {

    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        String username = args[0];
        while (!Thread.interrupted()) handleNextCommand(username);
    }

    private static void handleNextCommand(String username) throws IOException {
        System.out.print(username + ":> ");
        BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
        String command = commandLine.readLine();
        try (var socket = new Socket("localhost", 8888);
        var socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            socketWriter.write(username + ": " + command + "\n");
            socketWriter.flush();
            String incomingMessage = socketReader.readLine();
            if (incomingMessage == null) {
                throw new IOException("Server disconnected (end-of-stream)");
            }
            while (incomingMessage != null && !incomingMessage.contains("" + END_OF_TRANSMISSION + "")) {
                System.out.println(incomingMessage);
                incomingMessage = socketReader.readLine();
            }
        }
    }
}
