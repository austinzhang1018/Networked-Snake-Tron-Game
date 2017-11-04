/**
 * Created by austinzhang on 4/18/17.
 */
public class GridSquare {
    private boolean hasFood;
    private int snakePart;


    public GridSquare(boolean hasFood, int snakePart) {
        this.hasFood = hasFood;
        this.snakePart = snakePart;
    }

    public boolean hasFood() {
        return this.hasFood;
    }

    public boolean hasSnakePart() {
        return snakePart != 9;
    }

    public int getSnakePart() {
        return snakePart;
    }

    public void setFood(boolean food) {
        this.hasFood = food;
    }

    public void setSnakePart(int part) {
        this.snakePart = part;
    }

    @Override
    public String toString() {
        if (hasSnakePart()) {
            return getSnakePart() + "";
        }
        if (hasFood) {
            return "F";
        }
        return "O";
    }

    public GridSquare(GridSquare oldGridSquare) {
        this.hasFood = oldGridSquare.hasFood;
        this.snakePart = oldGridSquare.getSnakePart();
    }
}
