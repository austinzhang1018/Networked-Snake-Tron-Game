import java.util.LinkedList;

/**
 * Created by austinzhang on 4/18/17.
 */
public class Snake {
    private boolean isAlive;
    private LinkedList<Location> segmentLocations;
    private Direction direction;
    String name;

    public Snake(String name, Location location, Direction direction) {
        this.name = name;
        this.segmentLocations = new LinkedList<>();
        this.segmentLocations.add(location);
        isAlive = true;
        this.direction = direction;
    }

    public Location getHeadLocation() {
        return segmentLocations.get(0);
    }

    public void addHead(Location location) {
        segmentLocations.add(0, location);
    }

    public Location removeTail() {
        return segmentLocations.remove(segmentLocations.size() - 1);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Location> getSegmentLocations() {
        return segmentLocations;
    }

    public void removeAllSegments() {

        while (segmentLocations.size() != 0) {
            segmentLocations.remove(0);
        }
    }
}
