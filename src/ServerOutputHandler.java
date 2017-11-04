import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by austinzhang on 4/19/17.
 */
public class ServerOutputHandler {
    private PrintWriter out;

    public ServerOutputHandler(Socket socket) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void updateWinner(String winnerName, Map<String, Integer> scoreboard) {
        StringBuilder scoreboardMessage = new StringBuilder();

       for (String name : scoreboard.keySet()) {
           scoreboardMessage = new StringBuilder(" " + scoreboardMessage + name + ":" + scoreboard.get(name));
       }

        out.println("SCOR " + winnerName + scoreboardMessage.toString());
    }

    public void sendServerSnakeDirection(Direction direction) {
        String directionCode = null;

        if (direction.equals(Direction.UP)) {
            directionCode = "U";
        }
        else if (direction.equals(Direction.DOWN)) {
            directionCode = "D";
        }
        else if (direction.equals(Direction.LEFT)) {
            directionCode = "L";
        }
        else if (direction.equals(Direction.RIGHT)) {
            directionCode = "R";
        }

        out.println("PDIR " + directionCode);
    }

    public void endGame() {
        out.println("QUIT");
    }

    public void updateLocation(int row, int col, String type) {
        System.out.println("SENT: " + "r" + row + "c" + col);
        out.println("LOCU " + row + " " + col + " " + type);
    }

    public void clearBoard() {
        out.println("CLER");
    }

}
