import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by austinzhang on 4/19/17.
 */
public class ServerInputHandler extends Thread {
    private Socket socket;
    private Direction direction;
    private String playerName;
    private boolean isReady;


    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner line = new Scanner(in.readLine());  //wait for message from client

                String tag = line.next();

                if (tag.equals("NAME")) {
                    playerName = line.next();
                } else if (tag.equals("DIRC")){
                    String directionKey = line.next();
                    if (directionKey.equals("U")) {
                        direction = Direction.UP;
                    } else if (directionKey.equals("D")) {
                        direction = Direction.DOWN;
                    } else if (directionKey.equals("L")) {
                        direction = Direction.LEFT;
                    } else if (directionKey.equals("R")) {
                        direction = Direction.RIGHT;
                    }
                }
                else if (tag.equals("REDY")) {
                    isReady = true;
                }
                else {
                    throw new RuntimeException("INVALID TAG: " + tag);
                }
            } catch (IOException exception) {
                System.out.println("CONNECTION TO HOST LOST: Exiting...");
                System.exit(0);
            }
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public ServerInputHandler(Socket socket, Direction startingDirection) {
        this.socket = socket;
        this.direction = startingDirection;
        this.isReady = false;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isReady() {
        return isReady;
    }

    public void resetReadiness() {
        isReady = false;
    }
}
