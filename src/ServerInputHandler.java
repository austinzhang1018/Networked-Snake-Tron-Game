import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by austinzhang on 4/19/17.
 */
public class ServerInputHandler extends Thread {
    private Socket socket;
    private Direction direction;
    private String playerName;
    private boolean firstRead;


    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = in.readLine();  //wait for message from client

                if (firstRead) {
                    firstRead = false;
                    playerName = line;
                } else {
                    if (line.equals("U")) {
                        direction = Direction.UP;
                    } else if (line.equals("D")) {
                        direction = Direction.DOWN;
                    } else if (line.equals("L")) {
                        direction = Direction.LEFT;
                    } else if (line.equals("R")) {
                        direction = Direction.RIGHT;
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public ServerInputHandler(Socket socket, Direction startingDirection) {
        this.socket = socket;
        this.direction = startingDirection;
        firstRead = true;
    }

    public Direction getDirection() {
        return direction;
    }
}
