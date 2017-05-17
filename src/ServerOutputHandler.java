import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by austinzhang on 4/19/17.
 */
public class ServerOutputHandler extends Thread {
    private Socket socket;
    private String serializedGrid;
    private int delay;

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(serializedGrid);  //send message to client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ServerOutputHandler(Socket socket, String serializedGrid, int delay) {
        this.socket = socket;
        this.serializedGrid = serializedGrid;
        this.delay = delay;
    }

    public void updateSerializedGrid(String serializedGrid) {
        this.serializedGrid = serializedGrid;
    }

}
