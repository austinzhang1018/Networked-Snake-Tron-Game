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
import java.util.HashMap;
import java.util.Map;

/**
 * NEED TO MAKE COMMUNICATION TWO WAY SO GAME DOESN'T START TILL PEOPLE EXIT
 * START BY LOOKING AT PLAYER INPUT HANDLER
 */

public class Server {
    private static final int GAME_DELAY = 200;
    private static final int NUM_PLAYERS = 4;


    public static void main(String[] args) throws IOException, InterruptedException {
        boolean PLAY_SNAKE = false;

        Socket[] sockets = new Socket[NUM_PLAYERS];

        ServerInputHandler[] inputs = new ServerInputHandler[sockets.length];
        ServerOutputHandler[] outputs = new ServerOutputHandler[sockets.length];

        ServerSocket server = new ServerSocket(9000);  //start server on port 9000

        Game game = new Game(NUM_PLAYERS, GAME_DELAY, PLAY_SNAKE);

        for (int i = 0; i < sockets.length; i++) {
            sockets[i] = server.accept();
            inputs[i] = new ServerInputHandler(sockets[i], Direction.UP);
            outputs[i] = new ServerOutputHandler(sockets[i]);
            inputs[i].start();
            System.out.println("Client Connected");
        }

        String[] playerNames = new String[inputs.length];

        Thread.sleep(50);

        Map<String, Integer> scoreboard = new HashMap<String, Integer>();

        for (int i = 0; i < inputs.length; i++) {
            playerNames[i] = inputs[i].getPlayerName();
            scoreboard.put(playerNames[i], 0);
        }
        System.out.println(Arrays.toString(playerNames));


        GridDisplay display = new GridDisplay(10 * NUM_PLAYERS, 10 * NUM_PLAYERS);

        while (true) {
            game = new Game(NUM_PLAYERS, GAME_DELAY, PLAY_SNAKE);

            Thread.sleep(800);

            updateDisplay(display, game);

            sendInitialLoad(game.getGrid(), outputs);
            //loadBoard(game.getGrid(), outputs);

            Thread.sleep(800);

            game.setPlayerNames(playerNames);

            while (game.isStillPlaying()) {

                game.waitForDelay();

                GridSquare[][] oldGrid = new GridSquare[game.getGrid().length][game.getGrid()[0].length];

                for (int row = 0; row < oldGrid.length; row++) {
                    for (int col = 0; col < oldGrid[0].length; col++) {
                        oldGrid[row][col] = new GridSquare(game.getGrid()[row][col]);
                    }
                }

                Direction[] snakeDirections = new Direction[sockets.length];

                for (int i = 0; i < inputs.length; i++) {
                    snakeDirections[i] = inputs[i].getDirection();
                }
                game.setSnakeDirections(snakeDirections);
                game.playOneRound();
                updateDisplay(display, game);
                updateBoardLocations(oldGrid, game.getGrid(), outputs);
                for (int i = 0; i < inputs.length; i++) {
                    outputs[i].sendServerSnakeDirection(snakeDirections[i]);
                }
            }
            if (game.getWinner() != null) {
                scoreboard.put(game.getWinner(), scoreboard.get(game.getWinner())+ 1);
            }
            for (ServerOutputHandler output : outputs) {
                output.updateWinner(game.getWinner(), scoreboard);
            }

            boolean allReady = false;

            while (!allReady) {
                allReady = true;
                for (ServerInputHandler input : inputs) {
                    if (!input.isReady()) {
                        allReady = false;
                    }
                }
            }

            for (ServerOutputHandler output : outputs) {
                output.clearBoard();
            }

            for (ServerInputHandler input : inputs) {
                input.resetReadiness();
            }
        }

    }

    private static void loadBoard(GridSquare[][] grid, ServerOutputHandler[] outputs) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                for (ServerOutputHandler output : outputs) {
                    System.out.println("LOAD BOARD CALLED");
                    output.updateLocation(row, col, grid[row][col].toString());
                }
            }
        }
    }

    private static void updateBoardLocations(GridSquare[][] oldGrid, GridSquare[][] newGrid, ServerOutputHandler[] outputs) {
        for (int row = 0; row < oldGrid.length; row++) {
            for (int col = 0; col < oldGrid[0].length; col++) {
                if (!(oldGrid[row][col].toString().equals(newGrid[row][col].toString()))) {
                    for (ServerOutputHandler output : outputs) {
                        output.updateLocation(row, col, newGrid[row][col].toString());
                    }
                }
            }
        }
    }

    private static void sendInitialLoad(GridSquare[][] grid, ServerOutputHandler[] outputs) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col].hasSnakePart() || grid[row][col].hasFood()) {
                    for (ServerOutputHandler output : outputs) {
                        output.updateLocation(row, col, grid[row][col].toString());
                    }
                }
            }
        }
    }


    private static void updateDisplay(GridDisplay display, Game game) {
        for (int row = 0; row < game.getGrid().length; row++) {
            for (int col = 0; col < game.getGrid()[0].length; col++) {
                if (game.getGrid()[row][col].getSnakePart() != 9)
                    display.setColor(new Location(row, col), new Color(215, 10, 152));
                else
                    display.setColor(new Location(row, col), new Color(0, 0, 0));
            }
        }
    }

}
