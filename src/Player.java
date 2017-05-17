/**
 * Created by austinzhang on 4/17/17.
 */

import java.awt.*;
import java.io.*;  //for BufferedReader, InputStreamReader, PrintWriter
import java.net.*;  //for Socket

public class Player {

    public static void main(String args[]) throws IOException, InterruptedException {
        //Currently only up to 9 snakes can play so we don't really need more than 9 colors
        Color[] snakeColors = {new Color(100, 50, 5), new Color(100, 5, 50), new Color(245, 100, 5), new Color(50, 5, 100), new Color(5, 50, 100), new Color(5, 100, 50),
                new Color(15, 54, 200), new Color(94, 167, 134), new Color(150, 45, 150)
        };

        String lastGrid = "";

        GridDisplay display;

        display = new GridDisplay(20, 20);

        String playerName = display.showInputDialog("What's your username?");

        Socket socket = new Socket("10.13.101.130", 9000); //connect to server on port 9000 192.168.1.30.

        KeyListener keyListener = new KeyListener(display, 10);

        PlayerInputHandler playerInputHandler = new PlayerInputHandler(socket);

        PlayerOutputHandler playerOutputHandler = new PlayerOutputHandler(socket, keyListener.getLastDirection(), playerName);


        playerInputHandler.start();
        playerOutputHandler.start();
        keyListener.start();

        while (playerInputHandler.getSerializedGrid() == null) {
            Thread.sleep(10);
        }

        while (!socket.isClosed()) {

            String newGrid = playerInputHandler.getSerializedGrid();

            if (!newGrid.equals(lastGrid)) {
                parseIntoBoard(playerInputHandler.getSerializedGrid(), display, snakeColors);
                lastGrid = newGrid;
            }

            playerOutputHandler.updateDirection(keyListener.getLastDirection());
        }

        keyListener.close();

        /*
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("lowercase");  //send message to server
        String line = in.readLine();  //wait for message from server
        System.out.println(line);  //print message from server

        //disconnect from server
        in.close();
        out.close();
        socket.close();
        */
    }

    public static void parseIntoBoard(String boardString, GridDisplay display, Color snakeColors[]) {
        int row = 0;
        int col = 0;

        for (int i = 0; i < boardString.length(); i++) {
            String character = boardString.substring(i, i + 1);
            if (character.equals("|")) {
                row++;
                col = 0;
            } else if (character.equals("F")) {
                display.setColor(new Location(row, col), new Color(10, 200, 20));
                col++;
            }  else if (character.equals("O")) {
                display.setColor(new Location(row, col), new Color(0, 0, 0));
                col++;
            }
            else {
                display.setColor(new Location(row, col), snakeColors[Integer.parseInt(character)]);
                col++;
            }
        }
    }

}
