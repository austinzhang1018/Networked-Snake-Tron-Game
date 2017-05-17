/**
 * Created by austinzhang on 4/19/17.
 */
public class KeyListener extends Thread {
    private GridDisplay display;
    private int millisBetweenPolls;
    private Direction lastDirection;
    private boolean stillRunning;

    @Override
    public void run() {
        while (stillRunning) {
            int lastKeyPressed = display.checkLastKeyPressed();

            if (lastKeyPressed == 38) {
                this.lastDirection = Direction.UP;
            } else if (lastKeyPressed == 40) {
                this.lastDirection = Direction.DOWN;
            } else if (lastKeyPressed == 37) {
                this.lastDirection = Direction.LEFT;
            } else if (lastKeyPressed == 39) {
                this.lastDirection = Direction.RIGHT;
            }

            try {
                Thread.sleep(millisBetweenPolls);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public KeyListener(GridDisplay display, int millisBetweenPolls) {
        this.display = display;
        this.millisBetweenPolls = millisBetweenPolls;
        this.lastDirection = Direction.UP;
        this.stillRunning = true;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void close() {
        stillRunning = false;
    }
}
