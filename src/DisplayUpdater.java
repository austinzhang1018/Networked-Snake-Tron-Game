import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Austin Zhang on 6/26/2017.
 * Making this to test theory on why packets are getting dropped on a TCP socket connection.
 * I believe that writing to the display is slowing down my thread for receiving packets to the point where the buffer is overflowing.
 * As such, instead of writing to the display in the same thread as my input thread, I'll be offloading
 * display update commands to a separate thread.
 */
public class DisplayUpdater extends Thread{
    GridDisplay display;
    Queue<LocColorPair> updateQueue;
    boolean keepRunning;
    public DisplayUpdater(GridDisplay display) {
        this.display = display;
        updateQueue = new LinkedList<LocColorPair>();
        keepRunning = true;
    }

    @Override
    public void run() {
        while (keepRunning) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (updateQueue.peek() != null) {
                LocColorPair square = updateQueue.remove();
                display.setColor(square.getLocation(), square.getColor());
            }
        }
    }

    public void addToQueue(LocColorPair square) {
        updateQueue.add(square);
    }
}
