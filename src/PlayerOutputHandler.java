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
    private int delay;
    private Direction currentDirection;

    public PlayerOutputHandler(Socket socket, Direction direction, String playerName, int delay) {
        this.socket = socket;
        this.direction = direction;
        this.playerName = playerName;
        firstRun = true;
        this.delay = delay;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {

            boolean oppositeDirection = false;
            if (currentDirection == Direction.UP && direction == Direction.DOWN ||
                    currentDirection == Direction.DOWN && direction == Direction.UP ||
                    currentDirection == Direction.LEFT && direction == Direction.RIGHT ||
                    currentDirection == Direction.RIGHT && direction == Direction.LEFT) {
                oppositeDirection = true;
            }

            if (direction != currentDirection && !oppositeDirection) {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    if (firstRun) {
                        out.println("NAME " + playerName);
                        firstRun = false;
                    } else {
                        if (direction == Direction.UP) {
                            out.println("DIRC U");  //send message to Server
                        } else if (direction == Direction.DOWN) {
                            out.println("DIRC D");
                        } else if (direction == Direction.LEFT) {
                            out.println("DIRC L");
                        } else if (direction == Direction.RIGHT) {
                            out.println("DIRC R");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connection to Host Lost: Ending Game...");
                    System.exit(1);
                }
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDirection(Direction direction) {
        this.direction = direction;
    }

    public void updateCurrentDirection(Direction direction) {
        this.currentDirection = direction;
    }
}
