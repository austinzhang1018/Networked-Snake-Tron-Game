/**
 * Created by Austin Zhang on 6/26/2017.
 */
public class LocColorPair {
    Color color;
    Location location;
    private static Color[] snakeColors = {new Color(100, 50, 5), new Color(100, 5, 50), new Color(245, 100, 5), new Color(50, 5, 100), new Color(5, 50, 100), new Color(5, 100, 50),
            new Color(15, 54, 200), new Color(94, 167, 134), new Color(150, 45, 150)};


    public LocColorPair(Location location, String type) {
        this.location = location;
        if (type.equals("F")) {
            this.color = new Color(10, 200, 20);
        } else if (type.equals("O")) {
            this.color = new Color(0, 0, 0);
        } else {
            this.color = snakeColors[Integer.parseInt(type)];
        }
    }

    public Location getLocation() { return location; }

    public Color getColor() {
        return color;
    }
}
