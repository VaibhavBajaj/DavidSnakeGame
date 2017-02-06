/*  JPanel subclass that holds a target GameBoard and a KeyListener.
 *  The main method in this  class  is  tick(),  which  redraws  the  target
 *  GameBoard as a grid of colored rectangles (simplistic graphics for now),
 *  and calls nextTurn on the target GameBoard. The KeyListener attached  to
 *  the  class  translates  KeyEvents  received into commands for the target
 *  GameBoard's snake.
 */

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;

import javax.swing.JPanel;

class SnakePanel extends JPanel {
    private static final Color
        EVEN_COLOR = new Color(225, 225, 225),
        ODD_COLOR = new Color(217, 217, 218),
        SNAKE_HEAD_COLOR = new Color(0, 144, 60),
        SNAKE_BODY_COLOR = new Color(0, 180, 0),
        FOOD_COLOR = new Color(220, 0, 255),
        GAME_OVER_COLOR = new Color(80, 0, 0);
    
    private static int FONT_SIZE = 48;
    private static final Font font =
        new Font("SansSerif", Font.PLAIN, FONT_SIZE);
    
    private GameBoard board;
    private int xDim, yDim;
    private int boardWidth, boardHeight;
    private boolean gameRunning;
    private Listener listener;
    
    public SnakePanel(GameBoard boardArg, int xArg, int yArg) {
        board = boardArg;
        xDim = xArg;
        yDim = yArg;
        boardWidth = boardArg.getWidth();
        boardHeight = boardArg.getHeight();
        gameRunning = true;
        setPreferredSize(new Dimension(xArg, yArg));
        addKeyListener(listener = new Listener(board));
    }
    
    // Draw the target GameBoard as a grid of rectangles.
    // Draw the string "GameOver" as well if the game is over.
    @Override public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        
        for (int x = 0; x < boardWidth; ++x) {
            for (int y = 0; y < boardHeight; ++y) {
                switch (board.getTile(x, y)) {
                  default:
                    if (true) throw new RuntimeException(
                        "GameBoard board.getTile returned unexpected code");
                  break; case GameBoard.EMPTY:
                    // Checkerboard pattern.
                    gr.setColor(((x+y)%2 == 0) ? EVEN_COLOR : ODD_COLOR);
                  break; case GameBoard.SNAKE_HEAD:
                    gr.setColor(SNAKE_HEAD_COLOR);
                  break; case GameBoard.SNAKE_BODY:
                    gr.setColor(SNAKE_BODY_COLOR);
                  break; case GameBoard.FOOD:
                    gr.setColor(FOOD_COLOR);
                }
                drawTileRectangle(gr, x, y);
            }
        }
        if (!gameRunning) {
            gr.setColor(GAME_OVER_COLOR);
            gr.setFont(font);
            gr.drawString(
                "GAME OVER: score " + board.getSnakeLength(), 0, FONT_SIZE);
        }
    }
    
    // Draw a tile in the grid of rectangles that constitutes our simplistic
    // graphics in the location that corresponds to the location (x, y) of
    // the target GameBoard using the current color of gr.
    private void drawTileRectangle(Graphics gr, int x, int y) {
        int left = (int)Math.round((double)xDim * x / boardWidth);
        int right = (int)Math.round((double)xDim * (x+1) / boardWidth);
        int ht = boardHeight;
        int bottom = (int)Math.round((double)yDim * (ht-y) / ht);
        int top = (int)Math.round((double)yDim * (ht-y-1) / ht);
        gr.fillRect(left, top, right-left, bottom-top);
    }
    
    public void tick() {
        gameRunning = board.nextTurn(listener.popCommand());
        repaint();
    }
    
    public boolean isGameRunning() {
        return gameRunning;
    }
    
    private static class Listener implements KeyListener {
        int directionCommand;
        GameBoard targetBoard;
        
        Listener(GameBoard boardArg) {
            directionCommand = GameBoard.UNCHANGED;
            targetBoard = boardArg;
        }
        // Return the direction that the user intends the snake to go in
        // (ignoring commands that the target GameBoard considered invalid),
        // and reset the directionCommand to UNCHANGED.
        public int popCommand() {
            int result = directionCommand;
            directionCommand = GameBoard.UNCHANGED;
            return result;
        }
        // If the intended command was not invalid, store the command as
        // the directionCommand, which will be returned by popCommand.
        public void keyPressed(KeyEvent e) {
            int potentialCommand = -1;
            switch (e.getKeyCode()) {
              default:
                if (true) return;
              break; case KeyEvent.VK_LEFT: case KeyEvent.VK_A:
                potentialCommand = GameBoard.LEFT;
              // I use dvorak, okay! ,AOE master race!
              break; case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: case KeyEvent.VK_E:
                potentialCommand = GameBoard.RIGHT;
              break; case KeyEvent.VK_DOWN: case KeyEvent.VK_S: case KeyEvent.VK_O:
                potentialCommand = GameBoard.DOWN;
              break; case KeyEvent.VK_UP: case KeyEvent.VK_W: case KeyEvent.VK_COMMA:
                potentialCommand = GameBoard.UP;
            }
            if (targetBoard.canChangeDirectionTo(potentialCommand)) {
                directionCommand = potentialCommand;
            }
        }
        // We don't care about these events.
        public void keyReleased(KeyEvent e) { }
        public void keyTyped(KeyEvent e) { }
    }
}
