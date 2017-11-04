/**
 * Created by austinzhang on 4/17/17.
 */

import java.io.*;  //for BufferedReader, InputStreamReader, PrintWriter
import java.net.*;  //for Socket

public class Player {

    /**
     * CHANGE THIS FOR THE NUMBER OF PLAYERS
     */
    private static final int NUM_PLAYERS = 4;

    /**
     * CHANGE THIS FOR THE NUMBER OF PLAYERS
     */

    private static final int UPLOAD_DELAY = 10;

    public static void main(String args[]) throws IOException, InterruptedException {
        GridDisplay display;

        display = new GridDisplay(10 * NUM_PLAYERS, 10 * NUM_PLAYERS);

        display.setLineColor(new Color(0,0,0));

        display.setName("NetSnek");

        String playerName = display.showInputDialog("What's your username?");

        Socket socket = new Socket("192.168.235.1", 9000); //connect to server on port 9000 192.168.1.30.

        KeyListener keyListener = new KeyListener(display, 10);

        PlayerInputHandler playerInputHandler = new PlayerInputHandler(socket, display);

        PlayerOutputHandler playerOutputHandler = new PlayerOutputHandler(socket, keyListener.getLastDirection(), playerName, UPLOAD_DELAY);

        playerInputHandler.start();
        playerOutputHandler.start();
        keyListener.start();

        while (!socket.isClosed()) {
            playerOutputHandler.updateDirection(keyListener.getLastDirection());
            //The Player's current direction as pulled from the server
            playerOutputHandler.updateCurrentDirection(playerInputHandler.getCurrentDirection());
        }

        keyListener.close();
    }
}
