import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by austinzhang on 4/19/17.
 */
public class PlayerInputHandler extends Thread {
    private Socket socket;
    private GridDisplay display;
    private Direction currentDirection;

    //Currently only up to 9 snakes can play so we don't really need more than 9 colors
    private Color[] snakeColors = {new Color(100, 50, 5), new Color(100, 5, 50), new Color(245, 100, 5), new Color(50, 5, 100), new Color(5, 50, 100), new Color(5, 100, 50),
            new Color(15, 54, 200), new Color(94, 167, 134), new Color(150, 45, 150)
    };

    public PlayerInputHandler(Socket socket, GridDisplay display) {
        this.socket = socket;
        this.display = display;
        this.currentDirection = null;
    }

    @Override
    public void run() {
        DisplayUpdater dupdater = new DisplayUpdater(display);
        dupdater.start();
        while (!socket.isClosed()) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String readLine = in.readLine();
                System.out.println(this + ":  " + readLine);
                Scanner message = new Scanner(readLine);

                String tag = message.next();

                //System.out.println("Message Received: " + tag);

                if (tag.equals("LOCU")) {
                    int row = Integer.parseInt(message.next());
                    int col = Integer.parseInt(message.next());
                    String type = message.next();
                    //changeColorAtLocation(row, col, type);
                    dupdater.addToQueue(new LocColorPair(new Location(row, col), type));
                    //System.out.println(row + "," + col + " " + type);
                } else if (tag.equals("SCOR")) {
                    String lastGameWinner = message.next();

                    ArrayList<String> rankings = new ArrayList<String>();

                    while (message.hasNext()) {
                        rankings.add(message.next());
                    }

                    String messageDialog;
                    //Game ended in a tie.
                    if (lastGameWinner.equals("null")) {
                        messageDialog = "It's a tie, nobody wins.\nLeaderboard:";
                    } else {
                        messageDialog = lastGameWinner + " " + "wins!\nLeaderboard:";
                    }
                    for (int i = 0; i < rankings.size(); i++) {
                        messageDialog = messageDialog + "\n" + rankings.get(i);
                    }
                    display.showMessageDialog(messageDialog);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("REDY");
                } else if (tag.equals("QUIT")) {
                    if (!message.hasNext()) {
                        display.showMessageDialog("Game Over.");
                        System.exit(1);
                    } else {
                        display.showMessageDialog(message.next() + " Wins! Game Over.");
                        System.exit(1);
                    }
                } else if (tag.equals("PDIR")) {
                    currentDirection = getDirection(message.next());
                }
                else if (tag.equals("CLER")) {
                    for (int rows = 0; rows < display.getNumRows(); rows++) {
                        for (int cols = 0; cols < display.getNumCols(); cols++) {
                            display.setColor(new Location(rows, cols), new Color(0,0,0));
                        }
                    }
                    System.out.println("CLEARED");
                }
            } catch (IOException exception) {
                display.showMessageDialog("Lost Connection to Host. Click OK to quit.");
                System.exit(1);
            }
        }
    }

    private void changeColorAtLocation(int row, int col, String type) {

    }

    private Direction getDirection(String direction) {
        if (direction.equals("U")) {
            return Direction.UP;
        } else if (direction.equals("D")) {
            return Direction.DOWN;
        } else if (direction.equals("L")) {
            return Direction.LEFT;
        } else if (direction.equals("R")) {
            return Direction.RIGHT;
        }
        else {
            throw new RuntimeException("INVALID DIRECTION KEY: " + direction);
        }
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }
}
