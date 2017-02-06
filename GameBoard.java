// Class that represents the board and snake in a game of snake.
// The class holds a grid of arbitrary width and height, and keeps track
// of where the snake head and body is, and where the food is.
// The game uses a cartesian coordinate system (+y is up).

public class GameBoard {
    public static final int UNCHANGED=0, LEFT=1, RIGHT=2, DOWN=3, UP=4;
    public static final int EMPTY=0, SNAKE_HEAD=1, SNAKE_BODY=2, FOOD=3;
    
    private boolean gameOver;
    private int snakeDirection;
    private int snakeHeadX, snakeHeadY;
    private int foodX, foodY;
    private int width, height;
    private int snakeLength;
    private int currentTick;
    // board[x*height + y] holds the tick that tile x,y was
    // last touched by the snake.
    private int[] board;
    // Special arrays for use by the placeFood function. They store
    // the x and y coordinates of empty tiles in the board.
    private int[] placeFoodX, placeFoodY;
    
    // Construct a new gameboard with the specified dimensions and
    // initial snake length.
    public GameBoard(int widthArg, int heightArg, int snakeLengthArg) {
        if (widthArg <= 0) {
            throw new IllegalArgumentException("width must be positive");
        } else if (heightArg <= 0) {
            throw new IllegalArgumentException("height must be positive.");
        } else if (snakeLengthArg <= 0) {
            throw new IllegalArgumentException("snake has nonpositive length.");
        }
        
        gameOver = false;
        snakeDirection = UP;
        snakeHeadX = widthArg / 2; snakeHeadY = heightArg / 2;
        foodX = (int)(Math.random() * widthArg);
        foodY = (int)(Math.random() * heightArg);
        width = widthArg; height = heightArg;
        snakeLength = snakeLengthArg;
        currentTick = snakeLengthArg;
        board = new int[width * height];
        placeFoodX = new int[width * height];
        placeFoodY = new int[width * height];
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSnakeLength() { return snakeLength; }
    
    // Get what is occupying tile (x, y).
    public int getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= width) {
            throw new IndexOutOfBoundsException("getTile(" + x + "," + y + ")");
        }
        if (x == snakeHeadX && y == snakeHeadY) {
            return SNAKE_HEAD;
        }
        if (x == foodX && y == foodY) {
            return FOOD;
        }
        // tile (x, y) was last touched by the snake at touchedAt tick.
        // If it has been fewer than snakeLength ticks since the tile was
        // touched, then that tile has part of the snake's body on it.
        // Otherwise, the tile is empty.
        int touchedAt = board[x*height + y];
        if (currentTick < touchedAt + snakeLength) {
            return SNAKE_BODY;
        } else {
            return EMPTY;
        }
    }
    
    // Mark the tile at (x, y) as being touched.
    private void touchTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= width) {
            throw new IndexOutOfBoundsException("getTile(" + x + "," + y + ")");
        }
        board[x*height + y] = currentTick;
    }
    
    // Place the food on a random empty tile.
    private void placeFood() {
        // Store the coordinates of all empty tiles in placeFoodX and
        // placeFoodY. placeFoodX[i] and placeFoodY[i] store the x and
        // y coordinates (respectively) of the ith empty tile found.
        int tiles = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (getTile(x, y) == EMPTY) {
                    placeFoodX[tiles] = x;
                    placeFoodY[tiles] = y;
                    tiles++;
                }
            }
        }
        // There's no empty tile for food to be placed in.
        // Basically the game is over, so just set the food off the board.
        if (tiles == 0) {
            foodX = -1;
            foodY = -1;
        } else {
            int i = (int)(Math.random() * tiles);
            foodX = placeFoodX[i];
            foodY = placeFoodY[i];
        }
    }
    
    /*  Return true if direction would be a  valid  argument  to  nextTurn  that
     *  actually  changes  the  direction  that  the  snake  is traveling in. An
     *  argument is invalid if it is in the exact opposite  direction  that  the
     *  snake  is traveling in (e.g. UP when the snake is traveling DOWN), or if
     *  it would _immediately_ result in the snake crashing into  the  wall,  in
     *  order to reduce player frustration.
     */
    public boolean canChangeDirectionTo(int direction) {
        int dir = snakeDirection;
        
        switch (direction) {
          default:
            if (true) throw new IllegalArgumentException(
                "Unknown direction code");
          case UNCHANGED:
            return true;
          case LEFT:
            return snakeHeadX != 0 && dir != LEFT && dir != RIGHT;
          case RIGHT:
            return snakeHeadX != width-1 && dir != LEFT && dir != RIGHT;
          case UP:
            return snakeHeadY != height-1 && dir != UP && dir != DOWN;
          case DOWN:
            return snakeHeadY != 0 && dir != UP && dir != DOWN;
        }
    }
    /*  Advance the game by one turn. Moves the snake in the direction specified
     *  by directionArg, or, if directionArg is UNCHANGED, in the direction used
     *  in the previous tick. If the snake reaches the food, increase the length
     *  of  the  snake  by  one  and randomly place food on another tile. If the
     *  snake tries to go off the edge of the board or crashes into itself,  the
     *  game  is  over.  Returns true if the game is continuing, or false if the
     *  game is over.
     */
    public boolean nextTurn(int directionArg) {
        if (gameOver) {
            return false;
        }
        if (directionArg != UNCHANGED) {
            if (directionArg != snakeDirection
                && !canChangeDirectionTo(directionArg)
            ) {
                throw new IllegalArgumentException(
                    "Forbidden direction change.");
            }
            snakeDirection = directionArg;
        }
        // Calculate the new snake position.
        int newX = snakeHeadX, newY = snakeHeadY;
        switch (snakeDirection) {
          default:
            if (true) throw new RuntimeException(
                "Internal GameBoard error: unknown snakeDirection code.");
          break; case LEFT:
            newX--;
          break; case RIGHT:
            newX++;
          break; case DOWN:
            newY--;
          break; case UP:
            newY++;
        }
        if (newX < 0 || newX >= width || newY < 0 || newY >= width) {
            // Exiting board; game over!
            gameOver = true;
            return false;
        }
        // Perform an appropriate action based on what occupies the tile
        // that the snake is entering.
        switch (getTile(newX, newY)) {
          default:
            if (true) throw new RuntimeException(
                "Internal GameBoard error: unknown getTile return code");
          break; case EMPTY:
            // Everything's A-OK!
          break; case SNAKE_HEAD:
            if (true) throw new RuntimeException(
                "Internal GameBoard error: snake head crashed into itself?");
          break; case SNAKE_BODY:
            // Snake crashed into itself; game over!
            gameOver = true;
            if (true) return false;
          break; case FOOD:
            // Randomize food position and increase snake length.
            placeFood();
            snakeLength++;
        }
        // Advance to the next tick, mark the tile that the snake just
        // passed over, and set the new snake head position.
        ++currentTick;
        touchTile(newX, newY);
        snakeHeadX = newX;
        snakeHeadY = newY;
        return true;
    }
}

