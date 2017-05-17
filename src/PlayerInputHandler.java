import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by austinzhang on 4/19/17.
 */
public class PlayerInputHandler extends Thread {
    private Socket socket;
    private String serializedGrid;

    public PlayerInputHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                serializedGrid = in.readLine();  //wait for message from server
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public String getSerializedGrid() {
        return serializedGrid;
    }
}
