public class Location {
    private int row;
    private int col;

    public Location(int r, int c) {
        row = r;
        col = c;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean equals(Object otherLoc) {
        return row == ((Location)otherLoc).getRow() && col == ((Location)otherLoc).getCol();
    }

    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    public int hashCode() {
        return row * 10000 + col;
    }
}