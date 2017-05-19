import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by austinzhang on 4/19/17.
 */
public class KeyTester {
    public static void main(String[] args) throws InterruptedException, UnknownHostException {

        System.out.println(Inet4Address.getLocalHost().getHostAddress());

        GridDisplay display = new GridDisplay(5, 5);
        while (true) {
            Thread.sleep(10);
            int keyPressed = display.checkLastKeyPressed();
            if (keyPressed != -1) {
                System.out.println(keyPressed);
            }
            Location location = display.checkLastLocationClicked();

            if (location != null) {
                System.out.println(location.toString());
            }
        }
    }
}
