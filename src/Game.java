import java.util.HashMap;

/**
 * This is the local version of the snake game. Built for reference and comparison before making networked version.
 */
public class Game {
    public static void main(String[] args) throws InterruptedException {
        GridDisplay display = new GridDisplay(20, 20);
        Game game = new Game(2, 200, false);
        //game.start();

        while (game.isStillPlaying()) {
            Thread.sleep(10);


        }
    }

    private Direction[] snakeDirections;
    private Snake[] snakes;
    private GridSquare[][] grid;
    private boolean stillPlaying;
    private int delay;

    private boolean playSnake;

    private String winner;

    public GridSquare[][] getGrid() {
        return grid;
    }

    //NEED FINAL ARRAY LIST OF POSSIBLE SNAKE LOCATIONS

    //numPlayers is between 2 and 6
    public Game(int numPlayers, int delay, boolean playSnake) {
        this.playSnake = playSnake;
        this.delay = delay;
        snakeDirections = new Direction[numPlayers];

        stillPlaying = true;

        for (int i = 0; i < snakeDirections.length; i++) {
            snakeDirections[i] = Direction.UP;
        }

        grid = new GridSquare[numPlayers * 10][numPlayers * 10];

        snakes = new Snake[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            snakes[i] = (new Snake("Placeholder", new Location(2 + (int) (Math.random() * (grid.length - 2)), 2 + (int) (Math.random() * (grid[0].length - 2))), snakeDirections[i])); // NEEDS A LOCATION
        }

        //initializeGrid
        //First initialize everything as empty space
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = new GridSquare(false, 9);
            }
        }

        for (int i = 0; i < snakes.length; i++) {
            for (Location location: snakes[i].getSegmentLocations()) {
                grid[location.getRow()][location.getCol()].setSnakePart(i);
            }
        }

    }

    public void waitForDelay() throws InterruptedException {
        //DELAY
        Thread.sleep(delay);
    }

    public void playOneRound() throws InterruptedException {
        boolean keepPlaying = keepPlaying(snakes);

        if (keepPlaying) {
            for (int i = 0; i < snakes.length; i++) {
                snakes[i].setDirection(snakeDirections[i]);
            }

            if (playSnake) {
                spawnFood();
            }

            //Get everyone's next move

            Location[] nextMoves = nextMoves(snakes);

            //Remove the tail locations of snakes from the grid before killing players.
            for (Snake snake : snakes) {
                if (snake.isAlive()) {
                    Location tailLoc = snake.getSegmentLocations().get(snake.getSegmentLocations().size() - 1);

                    if (playSnake) {
                        grid[tailLoc.getRow()][tailLoc.getCol()].setSnakePart(9);
                    }
                }
            }


            //Check and see if the move is valid. If not, kill the player.
            //When the player is killed their entire snake disappears.
            killInvalidPlayers(nextMoves, snakes);

            //For the remaining players, check and see if they ate.
            //If the snake ate, keep its tail and add a head.
            //If the snake did not eat, delete its tail and add a head.
            for (int i = 0; i < snakes.length; i++) {
                if (snakes[i].isAlive()) {
                    snakes[i].addHead(nextMoves[i]);
                    GridSquare square = grid[nextMoves[i].getRow()][nextMoves[i].getCol()];
                    square.setSnakePart(i);
                    //If there's food grow by one by not reducing size
                    if (square.hasFood()) {
                        square.setFood(false);
                        square.setSnakePart(i);
                    } else {
                        if (playSnake) {
                            Location tailLoc = snakes[i].removeTail();
                        }
                    }
                }
            }

            //Determine if the game has ended
            keepPlaying = keepPlaying(snakes);
        }
        else {
            stillPlaying = false;

            //Announce winners if there are any.
            for (Snake snake : snakes) {
                if (snake.isAlive()) {
                    winner = snake.getName();
                    System.out.println(snake.getName() + " wins!");
                    return;
                }
            }

            System.out.println("No winners, it's a tie.");
        }

    }

    private Location[] nextMoves(Snake[] snakes) {

        Location[] locations = new Location[snakes.length];

        for (int i = 0; i < snakes.length; i++) {
            if (snakes[i].isAlive()) {
                if (snakes[i].getDirection().equals(Direction.DOWN)) {
                    locations[i] = new Location(snakes[i].getHeadLocation().getRow() + 1, snakes[i].getHeadLocation().getCol());
                }

                if (snakes[i].getDirection().equals(Direction.UP)) {
                    locations[i] = new Location(snakes[i].getHeadLocation().getRow() - 1, snakes[i].getHeadLocation().getCol());
                }

                if (snakes[i].getDirection().equals(Direction.LEFT)) {
                    locations[i] = new Location(snakes[i].getHeadLocation().getRow(), snakes[i].getHeadLocation().getCol() - 1);
                }

                if (snakes[i].getDirection().equals(Direction.RIGHT)) {
                    locations[i] = new Location(snakes[i].getHeadLocation().getRow(), snakes[i].getHeadLocation().getCol() + 1);
                }
            } else {
                locations[i] = null;
            }
        }


        for (int i = 0; i < locations.length; i++) {

            if (snakes[i].isAlive()) {

                int newRow = locations[i].getRow();
                int newCol = locations[i].getCol();


                if (locations[i] != null && (locations[i].getRow() < 0 || locations[i].getCol() < 0 || locations[i].getRow() >= grid.length || locations[i].getCol() >= grid.length)) {
                    if (locations[i].getRow() < 0) {
                        newRow = grid.length - 1;
                    } else if (locations[i].getRow() >= grid.length) {
                        newRow = 0;
                    }
                    if (locations[i].getCol() < 0) {
                        newCol = grid[0].length - 1;
                    } else if (locations[i].getCol() >= grid.length) {
                        newCol = 0;
                    }
                }

                locations[i] = new Location(newRow, newCol);

            }
        }

        return locations;
    }

    //While there are at least two snakes still alive, keep playing.
    public boolean keepPlaying(Snake[] snakes) {
        int numAlive = 0;

        for (Snake snake : snakes) {
            if (snake.isAlive()) {
                numAlive++;

                if (numAlive > 1) {
                    return true;
                }

            }
        }

        return false;
    }

    //Kills all the players that make invalid moves and removes them from
    private void killInvalidPlayers(Location[] nextMoves, Snake[] snakes) {

        //First check to see any snakes move off screen
        for (int i = 0; i < nextMoves.length; i++) {
            Location headLoc = nextMoves[i];
            if (headLoc != null && (headLoc.getRow() < 0 || headLoc.getCol() < 0 || headLoc.getRow() >= grid.length || headLoc.getCol() >= grid.length)) {
                if (snakes[i].isAlive()) {
                    snakes[i].setAlive(false);
                }
            }
        }


        //Kills snake if their nextLocation is the same as another snake's next location.
        HashMap<Location, Integer> locations = new HashMap<>();

        for (int i = 0; i < nextMoves.length; i++) {
            if (nextMoves[i] != null) {
                if (locations.containsKey(nextMoves[i])) {
                    snakes[locations.get(nextMoves[i])].setAlive(false);
                    snakes[i].setAlive(false);
                } else {
                    locations.put(nextMoves[i], i);
                }
            }
        }


        for (int i = 0; i < nextMoves.length; i++) {
            //Check to see if snake runs into other snake's body part.
            if (nextMoves[i] != null && snakes[i].isAlive() && grid[nextMoves[i].getRow()][nextMoves[i].getCol()].hasSnakePart()) {
                snakes[i].setAlive(false);
            }
        }

        //Erase the dead snakes from the board
        for (int i = 0; i < snakes.length; i++) {
            if (!snakes[i].isAlive()) {
                for (Location location : snakes[i].getSegmentLocations()) {
                    //grid[location.getRow()][location.getCol()].setSnakePart(9);
                }
                snakes[i].removeAllSegments();
            }
        }
    }

    private void spawnFood() {
        int amountFood = 0;

        for (GridSquare[] gridRow : grid) {
            for (GridSquare gridSquare : gridRow) {
                if (gridSquare.hasFood()) {
                    amountFood++;
                }
            }
        }

        if (amountFood < snakes.length + 4) {
            boolean tryAgain = true;

            while (tryAgain) {
                int row = (int) (Math.random() * grid.length);
                int col = (int) (Math.random() * grid[0].length);
                if (!grid[row][col].hasFood() && !grid[row][col].hasSnakePart()) {
                    grid[row][col].setFood(true);
                    tryAgain = false;
                }
            }
        }
    }

    public void setSnakeDirections(Direction[] snakeDirections) {
        this.snakeDirections = snakeDirections;
    }

    //@Override
    public void run() {
        /*
        try {
            play();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stillPlaying = false;
        */
    }

    public boolean isStillPlaying() {
        return stillPlaying;
    }

    public void setPlayerNames(String[] playerNames) {
        for (int i = 0; i < snakes.length; i++) {
            snakes[i].setName(playerNames[i]);
        }
    }

    public String getWinner() {
        return winner;
    }
}
