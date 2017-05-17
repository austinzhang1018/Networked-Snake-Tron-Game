import java.net.Socket;

/**
 * Created by austinzhang on 4/19/17.
 */
public class ServerPlayerHandler extends Thread {
    private Socket socket;
    private Direction direction;
    private String serializedGrid;
    private int outputDelay;
    private String playerName;

    @Override
    public void run() {
        ServerInputHandler serverInputHandler = new ServerInputHandler(socket, Direction.UP);
        ServerOutputHandler serverOutputHandler = new ServerOutputHandler(socket, serializedGrid, outputDelay);
        serverInputHandler.start();
        serverOutputHandler.start();

        while (!socket.isClosed()) {

            if (playerName == null) {
                playerName = serverInputHandler.getPlayerName();
            }

            direction = serverInputHandler.getDirection();
            serverOutputHandler.updateSerializedGrid(serializedGrid);
        }
    }

    public ServerPlayerHandler(Socket socket, String serializedGrid, int outputDelay) {
        this.socket = socket;
        direction = Direction.UP;
        this.outputDelay = outputDelay;
        this.serializedGrid = serializedGrid;
    }

    public Direction getDirection() {
        return direction;
    }

    public void updateSerializedGrid(String serializedGrid) {
        this.serializedGrid = serializedGrid;
    }

    public String getPlayerName() {
        System.out.println(playerName);
        return playerName;
    }
}
