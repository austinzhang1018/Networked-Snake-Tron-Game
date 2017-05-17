import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by austinzhang on 4/19/17.
 */
public class PlayerOutputHandler extends Thread {
    private Socket socket;
    private Direction direction;
    private String playerName;
    private boolean firstRun;

    public PlayerOutputHandler(Socket socket, Direction direction, String playerName) {
        this.socket = socket;
        this.direction = direction;
        this.playerName = playerName;
        firstRun = true;
    }

    @Override
    public void run() {
        Direction lastDirection = null;
        while (!socket.isClosed()) {
            if (direction != lastDirection) {
                lastDirection = direction;
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    if (firstRun) {
                        out.println(playerName);
                        firstRun = false;
                    } else {
                        if (direction == Direction.UP) {
                            out.println("U");  //send message to Server
                        } else if (direction == Direction.DOWN) {
                            out.println("D");
                        } else if (direction == Direction.LEFT) {
                            out.println("L");
                        } else if (direction == Direction.RIGHT) {
                            out.println("R");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateDirection(Direction direction) {
        this.direction = direction;
    }
}
