// Helper class and main method for the snake game app.

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.JFrame;

public class Snake {
    private static int turnLength = 280; // milliseconds per tick of game.
    
    // SnakePanel receives ticks from the game timer through this class.
    private static class TickSnakePanel implements ActionListener {
        private SnakePanel panel;
        TickSnakePanel(SnakePanel panelArg) {
            panel = panelArg;
        }
        public void actionPerformed(ActionEvent ignored) {
            panel.tick();
        }
    }
    
    public static void main(String[] args) {
        // Set up the frame and the SnakePanel that occupies the frame.
        JFrame frame = new JFrame("Snake");
        SnakePanel panel = new SnakePanel(new GameBoard(15, 15, 4), 720, 720);
        frame.setContentPane(panel);
        
        // Launch the game window.
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        
        // Set up the timer that periodically ticks the SnakePanel (which
        // itself advances the snake in the snake game). We need to supply
        // the first tick, since the timer has a delay before it provides
        // its first tick.
        panel.tick();
        Timer gameTimer = new Timer(turnLength, new TickSnakePanel(panel));
        gameTimer.start();
        
        // Quietly wait for the window to be closed, and make sure that the
        // focus stays on the SnakePanel (but only do this if the window
        // itself still has the focus, so as not to hijack the OS's focus
        // from another window if our game is not in the foreground).
        while (frame.isVisible()) {
            if (frame.hasFocus()) panel.requestFocus();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // It's fine, just keep waiting for the game to finish.
            }
        }
        // Stop the timer and exit. (I don't believe in EXIT_ON_CLOSE).
        gameTimer.stop();
        System.exit(0);
    }
}

