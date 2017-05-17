/**
 * Created by austinzhang on 4/17/17.
 * TODO: Code cleanup and optimization
 * TODO: Intro screen- room #, adaptive player sizing
 * TODO: Delete tail before figuring out whether someone is dead
 * TODO: Make it so snakes can't spawn on top of each other
 */

import java.io.*;  //for BufferedReader, InputStreamReader, PrintWriter
import java.net.*;  //for ServerSocket, Socket
import java.util.Arrays;

public class Server {
    private static final int MAP_UPLOAD_DELAY = 175;
    private static final int GAME_DELAY = 200;
    private static final int NUM_PLAYERS = 2;

    public static void main(String[] args) throws IOException, InterruptedException {
        Color[] snakeColors = {new Color(100, 50, 5), new Color(100, 5, 50), new Color(50, 100, 5), new Color(50, 5, 100), new Color(5, 50, 100), new Color(5, 100, 50),
                new Color(15, 54, 200), new Color(94, 167, 134), new Color(150, 45, 150)
        };

        Socket[] sockets = new Socket[NUM_PLAYERS];


        ServerPlayerHandler[] players = new ServerPlayerHandler[sockets.length];

        GridDisplay serverDisplay = new GridDisplay(NUM_PLAYERS * 10, NUM_PLAYERS * 10);

        ServerSocket server = new ServerSocket(9000);  //start server on port 9000

        Game game = new Game(NUM_PLAYERS, GAME_DELAY);

        String serializedGrid = serializeGrid(game.getGrid());

        for (int i = 0; i < sockets.length; i++) {
            sockets[i] = server.accept();
            players[i] = new ServerPlayerHandler(sockets[i], serializedGrid, MAP_UPLOAD_DELAY);
            players[i].start();
            System.out.println("Client Connected");
        }
        System.out.println("Reached");

        String[] playerNames = new String[players.length];

        Thread.sleep(50);

        for (int i = 0; i < players.length; i++) {
            System.out.println("Getplayernamescalled");
            playerNames[i] = players[i].getPlayerName();
        }
        System.out.println(Arrays.toString(playerNames));


        while (true) {
            game = new Game(NUM_PLAYERS, GAME_DELAY);

            game.setPlayerNames(playerNames);


            game.start();

            while (game.isStillPlaying()) {
                parseIntoBoard(serializeGrid(game.getGrid()), serverDisplay, snakeColors);

                Direction[] snakeDirections = new Direction[sockets.length];

                for (int i = 0; i < players.length; i++) {
                    players[i].updateSerializedGrid(serializeGrid(game.getGrid()));
                    snakeDirections[i] = players[i].getDirection();
                }
                game.setSnakeDirections(snakeDirections);
            }
        }

        //disconnect from client
//        for (Socket socket : sockets) {
//            socket.close();
//        }
    }

    public static String serializeGrid(GridSquare[][] grid) {
        boolean firstTime = true;

        //If O: NO FOOD NO SNAKE
        //If F: FOOD NO SNAKE
        //If S: NO FOOD SNAKE
        String string = "";
        for (int i = 0; i < grid.length; i++) {
            if (!firstTime) {
                string = string + "|";
            }
            firstTime = false;

            for (GridSquare gridSquare : grid[i]) {
                if (gridSquare.hasSnakePart()) {
                    string = string + gridSquare.getSnakePart();
                } else if (gridSquare.hasFood()) {
                    string = string + "F";
                } else {
                    string = string + "O";
                }
            }
        }

        return string;
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
            } else if (character.equals("O")) {
                display.setColor(new Location(row, col), new Color(0, 0, 0));
                col++;
            } else {
                display.setColor(new Location(row, col), snakeColors[Integer.parseInt(character)]);
                col++;
            }
        }
    }


}
