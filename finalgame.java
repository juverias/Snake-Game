import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class finalgame extends JFrame  {
    
    private JFrame gameFrame;//ONE FRAME CREATION

// DRAWING SNAKE
    // DECLARING VARIABLES FOR DIRECTION
    public boolean left = false;
    public boolean right = true; // initially the snake moves in the right direction
    public boolean up = false;
    public boolean down = false;
    // DECLARING AN ARRAYLIST FOR STORING BODY OF SNAKE
    public List<Integer> snakexlength;
    public List<Integer> snakeylength;
    // DECLARING VARIABLES FOR STORING INITIAL LENGTH OF SNAKE
    public int lengthsnake = 3;
    // DECLARING VARIABLE FOR CHECKING INITIAL STAGE OF SNAKE
    public int moves = 0;
    // TITLE IMAGE
    public ImageIcon snaketitle = new ImageIcon(getClass().getResource("snaketitle.jpg"));
    // SETTING ICONS FOR SNAKE HEADS
    public ImageIcon leftmouth = new ImageIcon(getClass().getResource("leftmouth.png"));
    public ImageIcon rightmouth = new ImageIcon(getClass().getResource("rightmouth.png"));
    public ImageIcon upmouth = new ImageIcon(getClass().getResource("upmouth.png"));
    public ImageIcon downmouth = new ImageIcon(getClass().getResource("downmouth.png"));
    // SNAKE IMAGE FOR BODY
    public ImageIcon snakeimage = new ImageIcon(getClass().getResource("snakeimage.png"));
    // ENEMY IMAGE
    public ImageIcon enemy = new ImageIcon(getClass().getResource("enemy.png"));
    //TROPHY IMAGE
    public ImageIcon trophy = new ImageIcon(getClass().getResource("trophy.png"));

    // VARIABLES FOR ENEMY POSITION
    public int enemyXPos;
    public int enemyYPos;
    // MOVING SNAKE AFTER EVERY MILISECOND 
    // Define timers for each level
    private Timer easyTimer;
    private Timer mediumTimer;
    private Timer hardTimer;

    // Define delay values for each level
    private int easyDelay = 150;
    private int mediumDelay = 110;
    private int hardDelay = 100;

    //INITIALIZING THE CURRENT LEVEL TO NULL
    public Level currentLevel; 

    // VARIABLE TO CHECK IF THE GAME IS STARTED THEN ONLY MOVE THE SNAKE
    public boolean gameStarted = false;
    // VARIABLE FOR DRAWING SCORE ON SCREEN
    public int score = 0;
    // INITIALISES GAMEOVER
    public boolean gameOver = false;
    // VARIABLE FOR HIGH SCORE
    // Variables to store high scores for each level
    private int highScoreEasy;
    private int highScoreMedium;
    private int highScoreHard;

    public Preferences prefs;
    
    private JPanel levelPanel;
    //DECLARING 4 PANELS
    private GamePanelEasy gamePanelEasy;
    private GamePanelMedium gamePanelMedium;
    private GamePanelHard gamePanelHard;

    public JPanel container; // Declare containerPanel here 
    public CardLayout cardLayout;
    public JPanel levelCards;

    public finalgame() {  
    //CREATING FRAME 
    gameFrame  = new JFrame("Snake Game");
    gameFrame .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gameFrame .setSize(800, 600);
    gameFrame .setResizable(false);
    gameFrame .setLocationRelativeTo(null); // Center the frame
    
     // MANAGEMENT OF OF PANELS USING CARD LAYOUT 
     cardLayout = new CardLayout();
   
     // Create a container panel using CardLayout to hold all the panels
     container = new JPanel(cardLayout);
     levelCards = new JPanel(cardLayout);// level cards ie  panels(easy,medium,hard)

     create_level_Panel();//DECLARING LEVEL SELECTION PANEL CREATION METHOD   
     create_easy_levelPanel();//DECLARING EASY LEVEL PANEL METHOD
     create_medium_levelPanel();//DECLARING MEDIUM LEVEL PANEL METHOD
     create_hard_levelPanel();//DECLARING HARD LEVEL PANEL METHOD  
    
     container.setFocusable(true); // Set the panel as focusable
     container.requestFocusInWindow(); // Request focus for the panel

     gameFrame.add(container);// Add the container Panel to the gameFrame

     // Add the panels to the container panel
     container.add(levelPanel, "LevelPanel");
     container.add(gamePanelEasy, "GamePanelEasy"); 
     container.add(gamePanelMedium, "GamePanelMedium"); 
     container.add(gamePanelHard, "GamePanelHard");  
     gameFrame.setVisible(true);

     // Shows the level selection panel initially
     cardLayout.show(container, "LevelPanel"); 

     // Load high scores from preferences
     prefs = Preferences.userNodeForPackage(finalgame.class);
     highScoreEasy = prefs.getInt("HighScoreEasy", 0);
     highScoreMedium = prefs.getInt("HighScoreMedium", 0);
     highScoreHard = prefs.getInt("HighScoreHard", 0);

}

public void updateHighScores() {
    // Update high scores based on the current level
    switch (currentLevel) {
        case EASY:
            if (score > highScoreEasy) {
                highScoreEasy = score;
                prefs.putInt("HighScoreEasy", highScoreEasy);
            }
            break;
        case MEDIUM:
            if (score > highScoreMedium) {
                highScoreMedium = score;
                prefs.putInt("HighScoreMedium", highScoreMedium);
            }
            break;
        case HARD:
            if (score > highScoreHard) {
                highScoreHard = score;
                prefs.putInt("HighScoreHard", highScoreHard);
            }
            break;
    }
}
   public void resetGame(Timer timer) {
        gameOver = false;
        gameStarted = false;
        score = 0;
        lengthsnake = 3;
        moves = 0;
        timer.stop(); // Stop the timer
    }

    // Common restartGame method for all levels
    public void restartGame(Timer timer) {
        resetGame(timer);
        cardLayout.show(levelCards, "LevelPanel");// Switch to the "LevelPanel" card
        gamePanelEasy.setVisible(false); // Hide the easy game panel
        gamePanelMedium.setVisible(false);// Hide the medium game panel
        gamePanelHard.setVisible(false);// Hide the medium game panel
        levelPanel.setVisible(true);// Show the level selection panel
        levelPanel.requestFocus();// Set focus to the game panel
    }

    //CREATING LEVELS
    private enum Level {
        EASY, MEDIUM, HARD
    }

 // Public method to set the delay for the easy timer
 public void setEasyDelay(int delay) {
    easyDelay = delay;
    if (currentLevel == Level.EASY) {
        easyTimer.setDelay(easyDelay);
    }
}

// Public method to set the delay for the medium timer
public void setMediumDelay(int delay) {
    mediumDelay = delay;
    if (currentLevel == Level.MEDIUM) {
        mediumTimer.setDelay(mediumDelay);
    }
}

// Public method to set the delay for the hard timer
public void setHardDelay(int delay) {
    hardDelay = delay;
    if (currentLevel == Level.HARD) {
        hardTimer.setDelay(hardDelay);
    }
}


// Add a method to start the timer for the current level
public void startTimer() {
    if (currentLevel == Level.EASY) {
        easyTimer.start();
    } else if (currentLevel == Level.MEDIUM) {
        mediumTimer.start();
    } else if (currentLevel == Level.HARD) {
        hardTimer.start();
    }
}



    public void startGame(Level level) {
        currentLevel = level; // Set the currentLevel variable
        levelPanel.setVisible(false); // Hide the level selection panel
            switch (level) {
                case EASY:
                cardLayout.show(levelCards, "GamePanelEasy"); // Switch to the easy level panel
                gamePanelEasy.setVisible(true); // Show the "Easy" panel
                gamePanelEasy.requestFocus(); // Set focus to the game panel
                gamePanelEasy.startGame(); // Start the game when switching to the game panel
                break;          
                
                case MEDIUM:
                cardLayout.show(levelCards, "GamePanelMedium"); // Switch to the easy level panel
                gamePanelMedium.setVisible(true); // Show the "Easy" panel
                gamePanelMedium.requestFocus(); // Set focus to the game panel
                gamePanelMedium.startGame(); // Start the game when switching to the game panel
                break;

                case HARD:
                cardLayout.show(levelCards, "GamePanelHard"); // Switch to the easy level panel
                gamePanelHard.setVisible(true); // Show the "Easy" panel
                gamePanelHard.requestFocus(); // Set focus to the game panel
                gamePanelHard.startGame(); // Start the game when switching to the game panel
                break;

                default:
                    break;
            }
            gameFrame.setVisible(true); // Show the game frame
    }  

    private void  create_level_Panel(){ //DEFINING LEVEL SELECTION PANEL METHOD
        // SETTING LEVEL SELECTION PANEL
         // CREATION OF PANEL
         levelPanel = new JPanel();
         levelPanel.setSize(800, 600); // Match the size of the game frame);
         levelPanel.setLayout(null); // Use null layout
         levelPanel.setBackground(Color.BLACK); // sets the panel background color
         levelPanel.setVisible(true); // Make sure the level selection panel is initially visible
         setLocationRelativeTo(null); // Center the frame

         // ADDING PANELS TO CARD LAYOUT
         levelCards.add(levelPanel, "LevelPanel");
       
         //LOAD AND SET THE BACKGROUND IMAGE
         ImageIcon backgroundImageIcon = new ImageIcon("border.jpg"); // Replace with your image file path
         JLabel backgroundLabel = new JLabel(backgroundImageIcon);
         backgroundLabel.setBounds(00, 00, 784, 559); // Set bounds to cover the entire frame

         // SETTING FONT AND COLOR FOR "SELECT A LEVEL:" LABEL
         JLabel selectLevelLabel = new JLabel("Select a Level:");
         selectLevelLabel.setFont(new Font("Vivaldi", Font.BOLD, 70));
         selectLevelLabel.setForeground(Color.RED);
         selectLevelLabel.setBounds(140, 80, 600, 100); // Set bounds
        
         //SETTING A DEFAULT LABEL
         JLabel defaultLabel = new JLabel("Default:");//creates a default label
         defaultLabel.setFont(new Font("Ink free", Font.BOLD, 30));
         defaultLabel.setForeground(Color.white);
         defaultLabel.setBackground(Color.BLACK);
         defaultLabel.setBounds(180, 200, 300, 50); // Set bounds
        
         // CREATE BUTTONS AND SET BOUNDS
         JButton easyButton = createButton("Easy", 300, 200);
         JButton mediumButton = createButton("Medium", 300, 290);
         JButton hardButton = createButton("Hard", 300, 380);

         easyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame(Level.EASY);
              
            }
         });

         mediumButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame(Level.MEDIUM);
            }
         });

         hardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame(Level.HARD);
            }
        });

        levelPanel.add(selectLevelLabel);
        levelPanel.add(backgroundLabel); // Add the background image
        levelPanel.add(defaultLabel);
        levelPanel.add(easyButton);
        levelPanel.add(mediumButton);
        levelPanel.add(hardButton);
        levelPanel.setVisible(true);

        levelCards.add(levelPanel, "LevelPanel");
        levelCards.add(levelPanel, "GamePanelEasy");
        levelCards.add(levelPanel, "gamePanelMedium");
        levelCards.add(levelPanel, "gamePanelHard");
}

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 250, 70); // Set bounds for buttons
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Chiller", Font.BOLD, 30));
        button.setFocusPainted(false); // Prevent button highlighting
        return button;
    }
  
    public void create_easy_levelPanel(){
    // CREATION OF PANEL
         gamePanelEasy = new GamePanelEasy();
         gamePanelEasy.setSize(800, 600); // Match the size of the game frame);
         gamePanelEasy.setBackground(Color.darkGray); // sets the panel background color
         gamePanelEasy.setLayout(null); // Use null layout
         setLocationRelativeTo(null); // Center the frame
         setFocusable(true);  //sets the focus on the easy panel
         setFocusTraversalKeysEnabled(true);
    
    // ADDING PANEL TO CARD LAYOUT
         levelCards.add(gamePanelEasy, "GamePanelEasy");
         gamePanelEasy.setVisible(false); // Initially, hide the "Easy" panel
         gamePanelEasy.addKeyListener(gamePanelEasy);
    
}
      
public class GamePanelEasy extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
   
    public GamePanelEasy() {
  
          // INITIALIZING TIMER WITH EASY DELAY VALUE     
           timer = new Timer(easyDelay, this);

          // ADDING AND CREATING KEY LISTENER
          addKeyListener(this);
          setFocusable(true);
          setFocusTraversalKeysEnabled(true);
  
          // SETTING UP PROPERTIES OF SNAKE
          snakexlength = new ArrayList<>();
          snakeylength = new ArrayList<>();
  
          // STARTING THE GAME
          startGame();
  
          // LOAD HIGH SCORE FROM PREFERENCES
          prefs = Preferences.userNodeForPackage(finalgame.class);
   
    }
 

    private void startGame() {
        
        gameOver = false; // Set game over state to false
        gameStarted = true;
        initializeSnake();
        generateRandomPosition();
        timer.start(); // Start the timer
    }

     public void initializeSnake() {
        // Clear any previous data if needed
        snakexlength.clear();
        snakeylength.clear();
    
        // Set the initial position of the snake's head randomly within the black area
        Random rand = new Random();
    
        int panelWidth = 800; // Specify the panel width
        int panelHeight = 600; // Specify the panel height
    
        int initialX;
        int initialY;
    
        // Generate initial positions that don't collide with the snake's body and
        // ensure the initial position is at least one grid space away from the borders
        do {
            initialX = 25 + rand.nextInt((panelWidth - 50) / 25) * 25; // Random X position within the black area
            initialY = 100 + rand.nextInt((panelHeight - 125) / 25) * 25; // Random Y position within the black area
        } while (snakexlength.contains(initialX) || snakeylength.contains(initialY) || 
                 initialX <= 15 || initialX >= panelWidth - 25 ||
                 initialY <= 100 || initialY >= panelHeight - 25);
    
        // Set the initial position of the snake's head
        snakexlength.add(initialX);
        snakeylength.add(initialY);
    
        // Set initial direction of the snake (you can modify this based on your logic)
        left = false;
        right = true;
        up = false;
        down = false;
    
        // Set initial positions of the snake's body parts (adjust as needed)
        for (int i = 1; i < lengthsnake; i++) {
            if (right) {
                snakexlength.add(initialX - i * 25); // Adjust as needed
                snakeylength.add(initialY);
            } else if (left) {
                snakexlength.add(initialX + i * 25); // Adjust as needed
                snakeylength.add(initialY);
            } else if (up) {
                snakexlength.add(initialX);
                snakeylength.add(initialY + i * 25); // Adjust as needed
            } else if (down) {
                snakexlength.add(initialX);
                snakeylength.add(initialY - i * 25); // Adjust as needed
            }
        }
    }
    
 private void generateRandomPosition() {
         Random rand = new Random();
         int maxX = 750 - 25; // Maximum X position for enemy within the board size (right - left)
         int maxY = 480 - 25; // Maximum Y position for enemy within the board size (top - down)
         enemyXPos = 25 + rand.nextInt(maxX / 25) * 25; // Random X position within the board size
         enemyYPos = 100 + rand.nextInt(maxY / 25) * 25; // Random Y position within the board size
 }

// OVERRIDE A PAINT METHOD TO DRAW COMPONENTS ON JPANEL    
 @Override
 protected void paintComponent(Graphics g) {
     super.paintComponent(g);
        
     // Your drawing code here (same as your previous paint method)
              g.setColor(Color.white);// Border Color is set as White
              g.drawRect(15, 10, 755, 60);// Rectangle Border for title
              g.drawRect(15, 80, 755, 470);// Rectangle border for Game
              snaketitle.paintIcon(gameFrame, g, 18, 14);// adds image for title here x is space from left and y is space from top
              g.setColor(Color.black);// Sets the color as black
              g.fillRect(19, 83, 749, 464);// fill Rectangle border for Game as Black

     // DRAWING SNAKE HEAD AND BODY
              drawSnake(g);

     // DRAWING ENEMY
              enemy.paintIcon(gameFrame, g, enemyXPos, enemyYPos);

     // DRAWING THE TROPHY
              g.drawImage(trophy.getImage(), 530, 30, this); // Adjust the coordinates as needed         

     // DRAW SCORE
              g.setColor(Color.white);
              g.setFont(new Font("Ink Free", Font.BOLD, 20));
              g.drawString("Score: " + score, 70, 50);
              g.setColor(Color.white);
              g.setFont(new Font("Ink Free", Font.BOLD, 20));
              g.drawString("High Score: EASY " + highScoreEasy, 558, 50);

     // DRAW GAME OVER SCREEN
              if (gameOver) {
               gameOver(g);
               timer.stop(); // Stop the timer
             }
     // DISPOSING GRAPHICS
              g.dispose();
}
 
 public void drawSnake(Graphics g) {
         // DRAWING HEAD
             if (left) {
               leftmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (right) {
               rightmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (up) {
               upmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (down) {
               downmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             }

             // DRAWING BODY
             for (int i = 1; i < lengthsnake; i++) {
             snakeimage.paintIcon(gameFrame, g, snakexlength.get(i), snakeylength.get(i));
             }
 }   
  
// Add a new method for boundary checking
private boolean isSnakeOutOfBounds() {
    int headX = snakexlength.get(0);
    int headY = snakeylength.get(0);
    
    // Define the boundaries of the black area
    int minX = 25;
    int minY = 100;
    int maxX = 775; // Adjust this value based on your panel size
    int maxY = 575; // Adjust this value based on your panel size

    // Check if the snake's head is outside the black area boundaries
    return headX < minX || headX >= maxX || headY < minY || headY >= maxY;
}


 private void moveSnake() {
       // MOVING THE BODY OF SNAKE
          if (!gameOver) {// CHECKS IF GAME IS OVER OR NOT
         
              for (int i = lengthsnake - 1; i > 0; i--) {// last part of snake is moved
              snakexlength.set(i, snakexlength.get(i - 1));// adding the last part in the current position of snake
              snakeylength.set(i, snakeylength.get(i - 1));
             }

       // MOVE THE HEAD OF SNAKE BASED ON DIRECTION //CHANGES POSITION
          if (left) {
              snakexlength.set(0, snakexlength.get(0) - 25);
          } else if (right) {
              snakexlength.set(0, snakexlength.get(0) + 25);
          } else if (up) {
              snakeylength.set(0, snakeylength.get(0) - 25);
          } else if (down) {
              snakeylength.set(0, snakeylength.get(0) + 25);
          }
         
         
         // Check if the snake hits the boundaries of the black area
        // Check if the snake hits the boundaries
        if (isSnakeOutOfBounds()) {
            gameOver = true;
        }
    
    }
  }

 private void checkCollision() {
      // CHECK IF THE SNAKE HITS THE BOUNDARIES
      if (isSnakeOutOfBounds()) {
        gameOver = true;
    }
      // CHECK IF THE SNAKE EATS FOOD AND INCREASE SIZE AND SCORE
      if (snakexlength.get(0) == enemyXPos && snakeylength.get(0) == enemyYPos) {
           generateRandomPosition();
           increaseSnakeSize();
      }
      // CHECK IF THE SNAKE EATS ITSELF
      for (int i = 1; i < lengthsnake; i++) {
          if (snakexlength.get(i).equals(snakexlength.get(0)) && snakeylength.get(i).equals(snakeylength.get(0))) {
          // Checking if the body position is the same as the head
          gameOver = true; // Set game over state to true
          break;
       }
     }

}

 private void increaseSnakeSize() {
   int lastIndex = snakexlength.size() - 1;
   int prevX = snakexlength.get(lastIndex);
   int prevY = snakeylength.get(lastIndex);
   snakexlength.add(prevX);
   snakeylength.add(prevY);
   lengthsnake++;
   score++; // Increase the score by one

  // UPDATE HIGH SCORE BASED ON THE CURRENT LEVEL
  switch (currentLevel) {
    case EASY:
        if (score > highScoreEasy) {
            highScoreEasy = score;
            prefs.putInt("HighScoreEasy", highScoreEasy);
        }
        break;

    case MEDIUM:
        if (score > highScoreMedium) {
            highScoreMedium = score;
            prefs.putInt("HighScoreMedium", highScoreMedium);
        }
        break;

    case HARD:
        if (score > highScoreHard) {
            highScoreHard = score;
            prefs.putInt("HighScoreHard", highScoreHard);
        }
        break;

    default:
        break;
}
}

// CONTROLLING SNAKE BY PRESSING KEYS
@Override
public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
 
    if (!gameStarted) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //finalgame.this.startGame(currentLevel); // Start the game with the selected level
             cardLayout.show(levelCards, "LevelPanel"); // Switch to the "LevelPanel" card
             gamePanelEasy.setVisible(false); // Hide the game panel
             levelPanel.setVisible(true); // Show the level selection panel
             levelPanel.requestFocus(); // Set focus to the game panel
        }
        return; // Ignore other keys if the game hasn't started yet
    }
  if  (e.getKeyCode() == KeyEvent.VK_LEFT && !right) { // ie checking that if left key is pressed and the snake is
                                                    // not moving in the right
     left = true;
     right = false;
     up = false;
     down = false;
 }
 else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !left) { // ie checking that if right key is pressed and the snake is
                                                    // not moving in the left
     left = false;
     right = true;
     up = false;
     down = false;
 }
 else if (e.getKeyCode() == KeyEvent.VK_UP && !down) { // ie checking that if up key is pressed and the snake is not
                                                 // moving in the down
     left = false;
     right = false;
     up = true;
     down = false;
 }
 else if  (e.getKeyCode() == KeyEvent.VK_DOWN && !up) { // ie checking that if down key is pressed and the snake is not
                                                 // moving in the up
     left = false;
     right = false;
     up = false;
     down = true;
 }
 else if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
    finalgame.this.resetGame(timer); // Restart the game and show level selection
     }               
 }

@Override
public void keyTyped(KeyEvent e) {

}

@Override
public void keyReleased(KeyEvent e) {

}
 private void gameOver(Graphics g) {
      // GAME OVER TEXT
      g.setColor(Color.red);
      g.setFont(new Font("Ink Free", Font.BOLD, 75));
      FontMetrics metrics = getFontMetrics(g.getFont());
      g.drawString("Game Over", (750 - metrics.stringWidth("Game Over")) / 2, 480 / 2);

      // DRAW SCORE AFTER GAME OVER
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 60));
      FontMetrics metrics1 = getFontMetrics(g.getFont());
      g.drawString("Score: " + score, (750) - metrics1.stringWidth("Score: " + score) - 350, 340);

      // DRAW SCORE AFTER GAME OVER
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 30));
      FontMetrics metrics2 = getFontMetrics(g.getFont());
      g.drawString("Press Space to Restart Game",(750)-metrics2.stringWidth("Press Space to Restart Game ")-150, 450);

      // DRAW HIGH SCORE
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 20));
      FontMetrics metrics3 = getFontMetrics(g.getFont());
     
      // Display high scores for each level
      g.drawString("High Score - EASY: " + highScoreEasy, 430, 300);
      g.drawString("High Score - MEDIUM: " + highScoreMedium, 430, 330);
      g.drawString("High Score - HARD: " + highScoreHard, 430, 360);
 }

 
     @Override
     public void actionPerformed(ActionEvent e) {
           if (gameStarted && !gameOver) {
              moveSnake();
              checkCollision();
              repaint();
           }
    }

}


















 public void create_medium_levelPanel(){
    // CREATION OF PANEL
         gamePanelMedium = new GamePanelMedium();
         gamePanelMedium.setSize(800, 600); // Match the size of the game frame);
         gamePanelMedium.setBackground(Color.darkGray); // sets the panel background color
         gamePanelMedium.setLayout(null); // Use null layout
         setLocationRelativeTo(null); // Center the frame
         setFocusable(true);  //sets the focus on the easy panel
         setFocusTraversalKeysEnabled(true);
    
    // ADDING PANEL TO CARD LAYOUT
     levelCards.add(gamePanelMedium, "GamePanelMedium");
     gamePanelMedium.setVisible(false); // Initially, hide the "Easy" panel
     gamePanelMedium.addKeyListener(gamePanelMedium);
    
}

public class GamePanelMedium extends JPanel implements ActionListener, KeyListener {
    
    public ImageIcon block = new ImageIcon(getClass().getResource("block.png"));
    //CREATING AN ARRAY LIST FOR THE BLOCK
    public List<Block> blocks = new ArrayList<>();

    public ImageIcon vine = new ImageIcon(getClass().getResource("vines.png"));
    //CREATING AN ARRAY LIST FOR THE VINES
    public List<Vine> vines = new ArrayList<>();


class Block {
    int x;
    int y;
    int size;
    private ImageIcon blockImage;

    public Block(int x, int y, int size, ImageIcon blockImage) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.blockImage = blockImage;
    }

    public ImageIcon getImage() {
        return blockImage;
    }
}

class Vine {
    int x;
    int y;
    int width; // Width of the vine
    int height; // Height of the vine
    private ImageIcon vineImage;

    public Vine(int x, int y, int width, int height, ImageIcon vineImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vineImage = vineImage;
    }

    public ImageIcon getImage() {
        return vineImage;
    }
}

  private Timer timer;

   public GamePanelMedium() {
          // INITIALIZING TIMER
          timer = new Timer(mediumDelay, this);

          // ADDING AND CREATING KEY LISTENER
          addKeyListener(this);
          setFocusable(true);
          setFocusTraversalKeysEnabled(true);
  
          // SETTING UP PROPERTIES OF SNAKE
          snakexlength = new ArrayList<>();
          snakeylength = new ArrayList<>();
  
          // STARTING THE GAME
          startGame();
  
          // LOAD HIGH SCORE FROM PREFERENCES
          prefs = Preferences.userNodeForPackage(finalgame.class);
       
}
 

    private void startGame() {
        gameOver = false; // Set game over state to false
        gameStarted = true;
        initializeSnake();
        generateRandomPosition();
        generateRandomBlocks(); // Add this line to generate blocks and vines.
        timer.start(); // Start the timer
    }

    public void initializeSnake() {
        // Clear any previous data if needed
        snakexlength.clear();
        snakeylength.clear();
    
        // Set the initial position of the snake's head randomly within the black area
        Random rand = new Random();
    
        int panelWidth = 800; // Specify the panel width
        int panelHeight = 600; // Specify the panel height
    
        int initialX;
        int initialY;
    
        // Generate initial positions that don't collide with the snake's body and
        // ensure the initial position is at least one grid space away from the borders
        do {
            initialX = 25 + rand.nextInt((panelWidth - 50) / 25) * 25; // Random X position within the black area
            initialY = 100 + rand.nextInt((panelHeight - 125) / 25) * 25; // Random Y position within the black area
        } while (snakexlength.contains(initialX) || snakeylength.contains(initialY) || 
                 initialX <= 15 || initialX >= panelWidth - 25 ||
                 initialY <= 100 || initialY >= panelHeight - 25);
    
        // Set the initial position of the snake's head
        snakexlength.add(initialX);
        snakeylength.add(initialY);
    
        // Set initial direction of the snake (you can modify this based on your logic)
        left = false;
        right = true;
        up = false;
        down = false;
    
        // Set initial positions of the snake's body parts (adjust as needed)
        for (int i = 1; i < lengthsnake; i++) {
            if (right) {
                snakexlength.add(initialX - i * 25); // Adjust as needed
                snakeylength.add(initialY);
            } else if (left) {
                snakexlength.add(initialX + i * 25); // Adjust as needed
                snakeylength.add(initialY);
            } else if (up) {
                snakexlength.add(initialX);
                snakeylength.add(initialY + i * 25); // Adjust as needed
            } else if (down) {
                snakexlength.add(initialX);
                snakeylength.add(initialY - i * 25); // Adjust as needed
            }
        }
    }
    
    
    private void generateRandomPosition() {
        Random rand = new Random();
        int maxX = 750 - 25; // Maximum X position for enemy within the board size (right - left)
        int maxY = 480 - 25; // Maximum Y position for enemy within the board size (top - down)
    
        // Define a list to store the positions of random blocks
        List<Point> blockPositions = new ArrayList<>();
        for (Block block : blocks) {
            blockPositions.add(new Point(block.x, block.y));
        }
    
        do {
            enemyXPos = 25 + rand.nextInt(maxX / 25) * 25; // Random X position within the board size
            enemyYPos = 100 + rand.nextInt(maxY / 25) * 25; // Random Y position within the board size
        } while (blockPositions.contains(new Point(enemyXPos, enemyYPos)));
    
        // Now, enemyXPos and enemyYPos contain a position that doesn't overlap with the blocks
    }
    

// OVERRIDE A PAINT METHOD TO DRAW COMPONENTS ON JPANEL    
 @Override
 protected void paintComponent(Graphics g) {
     super.paintComponent(g);
        
     // Your drawing code here (same as your previous paint method)
              g.setColor(Color.white);// Border Color is set as White
              g.drawRect(15, 10, 755, 60);// Rectangle Border for title
              g.drawRect(15, 80, 755, 470);// Rectangle border for Game
              snaketitle.paintIcon(gameFrame, g, 18, 14);// adds image for title here x is space from left and y is space from top
              g.setColor(Color.black);// Sets the color as black
              g.fillRect(19, 83, 749, 464);// fill Rectangle border for Game as Black

     // DRAWING SNAKE HEAD AND BODY
              drawSnake(g);

     // DRAWING ENEMY
              enemy.paintIcon(gameFrame, g, enemyXPos, enemyYPos);

     // DRAWING THE TROPHY
              g.drawImage(trophy.getImage(), 510, 30, this); // Adjust the coordinates as needed        

     // DRAW SCORE
              g.setColor(Color.white);
              g.setFont(new Font("Ink Free", Font.BOLD, 20));
              g.drawString("Score: " + score, 68, 50);
              g.setColor(Color.white);
              g.setFont(new Font("Ink Free", Font.BOLD, 20));
              g.drawString("High Score: MEDIUM: " + highScoreMedium, 540, 50);

     // DRAW GAME OVER SCREEN
              if (gameOver) {
               gameOver(g);
               timer.stop(); // Stop the timer
             }

    // DRAW BLOCKS AND VINES
            paintBlocks(g);
    // DISPOSING GRAPHICS
              g.dispose();

}
 
 public void drawSnake(Graphics g) {
         // DRAWING HEAD
             if (left) {
               leftmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (right) {
               rightmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (up) {
               upmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (down) {
               downmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             }

             // DRAWING BODY
             for (int i = 1; i < lengthsnake; i++) {
             snakeimage.paintIcon(gameFrame, g, snakexlength.get(i), snakeylength.get(i));
             }
 }  

  public void paintBlocks(Graphics g) {
    for (Block block : blocks) {
        int blockSize = block.size;
        ImageIcon blockImage = block.getImage();
        if (blockImage != null) {
            g.drawImage(blockImage.getImage(), block.x, block.y, blockSize, blockSize, gamePanelHard);
        } else {
            System.err.println("Block image is null!");
        }
    }

    for (Vine vine : vines) {
        int vineWidth = vine.width;
        int vineHeight = vine.height;
        ImageIcon vineImage = vine.getImage();
        if (vineImage != null) {
            g.drawImage(vineImage.getImage(), vine.x, vine.y, vineWidth, vineHeight, gamePanelHard);
        } else {
            System.err.println("Vine image is null!");
        }
    }

}

private void generateRandomBlocks() {
    Random rand = new Random();
    blocks.clear(); // Clear existing blocks
    vines.clear();  // Clear existing vines

    int maxLines = 3; // Maximum number of lines of blocks

    int maxX = (750 - 25) / 25 * 25; // Adjust the maximum X position if needed
    int maxY = (480 - 25) / 25 * 25; // Adjust the maximum Y position if needed

    int minLineSize = 3; // Minimum number of blocks in a line
    int maxLineSize = 5; // Maximum number of blocks in a line

    int blockSize = 25; // Size of each block

    for (int line = 0; line < maxLines; line++) {
        // Randomly determine the orientation of the line (horizontal or vertical)
        boolean isHorizontal = rand.nextBoolean();

        // Randomly determine the starting position of the line within the black area
        int startX = 25 + rand.nextInt(maxX / 25) * 25;
        int startY = 100 + rand.nextInt(maxY / 25) * 25;

        // Generate a random line size within the specified range
        int lineSize = minLineSize + rand.nextInt(maxLineSize - minLineSize + 1);

        // Calculate the size of the entire line
        int lineSizePixels = lineSize * blockSize;

        // Adjust the starting position to ensure the entire line fits within the bounds
        if (isHorizontal) {
            startX = Math.min(startX, 750 - lineSizePixels);
        } else {
            startY = Math.min(startY, 480 - lineSizePixels);
        }

        // Generate a line of blocks with the same size
        for (int i = 0; i < lineSize; i++) {
            int blockX = startX + (isHorizontal ? i * blockSize : 0);
            int blockY = startY + (isHorizontal ? 0 : i * blockSize);

            blocks.add(new Block(blockX, blockY, blockSize, block));
        }
    }

    // Generate random vines
    int numVines = rand.nextInt(3) + 1; // Number of vines (1 to 3)

    for (int i = 0; i < numVines; i++) {
        int vineX = 25 + rand.nextInt(maxX / 25) * 25;
        int vineY = 100 + rand.nextInt(maxY / 25) * 25;

        // Adjust vine size to 25x90 pixels
        int vineWidth = 25;
        int vineHeight = 140;
        vines.add(new Vine(vineX, vineY, vineWidth, vineHeight, vine));
    }
}





 // Add a new method for boundary checking
  private boolean isSnakeOutOfBounds() {
    return snakexlength.get(0) < 15 || snakexlength.get(0) >= 749 || 
           snakeylength.get(0) < 100 || snakeylength.get(0) >= 510;
    }

 private void moveSnake() {
       // MOVING THE BODY OF SNAKE
          if (!gameOver) {// CHECKS IF GAME IS OVER OR NOT
              for (int i = lengthsnake - 1; i > 0; i--) {// last part of snake is moved
              snakexlength.set(i, snakexlength.get(i - 1));// adding the last part in the current position of snake
              snakeylength.set(i, snakeylength.get(i - 1));
             }

       // MOVE THE HEAD OF SNAKE BASED ON DIRECTION //CHANGES POSITION
          if (left) {
              snakexlength.set(0, snakexlength.get(0) - 25);
          } else if (right) {
              snakexlength.set(0, snakexlength.get(0) + 25);
          } else if (up) {
              snakeylength.set(0, snakeylength.get(0) - 25);
          } else if (down) {
              snakeylength.set(0, snakeylength.get(0) + 25);
          }
         
         
          // Check if the snake hits the boundaries of the black area
        // Check if the snake hits the boundaries
        if (isSnakeOutOfBounds()) {
            gameOver = true;
        }
    
    }
  }

 private void checkCollision() {
     // CHECK IF THE SNAKE HITS THE BOUNDARIES
      if (isSnakeOutOfBounds()) {
        gameOver = true;
    }
      // CHECK IF THE SNAKE EATS FOOD AND INCREASE SIZE AND SCORE
      if (snakexlength.get(0) == enemyXPos && snakeylength.get(0) == enemyYPos) {
           generateRandomPosition();
           increaseSnakeSize();
      }
      // CHECK IF THE SNAKE EATS ITSELF
      for (int i = 1; i < lengthsnake; i++) {
          if (snakexlength.get(i).equals(snakexlength.get(0)) && snakeylength.get(i).equals(snakeylength.get(0))) {
          // Checking if the body position is the same as the head
          gameOver = true; // Set game over state to true
          break;
       }
     }

      // CHECK IF THE SNAKE HITS A BLOCK
       for (Block block : blocks) {
        if (snakexlength.get(0) == block.x && snakeylength.get(0) == block.y) {
        gameOver = true; // Set game over state to true if snake hits a block
        break;
       }
     }

}

 private void increaseSnakeSize() {
   int lastIndex = snakexlength.size() - 1;
   int prevX = snakexlength.get(lastIndex);
   int prevY = snakeylength.get(lastIndex);
   snakexlength.add(prevX);
   snakeylength.add(prevY);
   lengthsnake++;
   score++; // Increase the score by one

 // UPDATE HIGH SCORE BASED ON THE CURRENT LEVEL
 switch (currentLevel) {
    case EASY:
        if (score > highScoreEasy) {
            highScoreEasy = score;
            prefs.putInt("HighScoreEasy", highScoreEasy);
        }
        break;

    case MEDIUM:
        if (score > highScoreMedium) {
            highScoreMedium = score;
            prefs.putInt("HighScoreMedium", highScoreMedium);
        }
        break;

    case HARD:
        if (score > highScoreHard) {
            highScoreHard = score;
            prefs.putInt("HighScoreHard", highScoreHard);
        }
        break;

    default:
        break;
}
}

// CONTROLLING SNAKE BY PRESSING KEYS
@Override
public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
 
    if (!gameStarted) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //finalgame.this.startGame(currentLevel); // Start the game with the selected level
             cardLayout.show(levelCards, "LevelPanel"); // Switch to the "LevelPanel" card
             gamePanelHard.setVisible(false); // Hide the game panel
             levelPanel.setVisible(true); // Show the level selection panel
             levelPanel.requestFocus(); // Set focus to the game panel
        }
        return; // Ignore other keys if the game hasn't started yet
    }
    if  (e.getKeyCode() == KeyEvent.VK_LEFT && !right) { // ie checking that if left key is pressed and the snake is
                                                    // not moving in the right
          left = true;
          right = false;
          up = false;
          down = false;
    }
    else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !left) { // ie checking that if right key is pressed and the snake is
                                                    // not moving in the left
          left = false;
          right = true;
          up = false;
          down = false;
    }
    else if (e.getKeyCode() == KeyEvent.VK_UP && !down) { // ie checking that if up key is pressed and the snake is not
                                                 // moving in the down
         left = false;
         right = false;
         up = true;
         down = false;
    }
    else if  (e.getKeyCode() == KeyEvent.VK_DOWN && !up) { // ie checking that if down key is pressed and the snake is not
                                                 // moving in the up
         left = false;
         right = false;
         up = false;
         down = true;
   }
    else if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
        finalgame.this.resetGame(timer);// Restart the game and show level selection
     }               
 }

@Override
public void keyTyped(KeyEvent e) {

}

@Override
public void keyReleased(KeyEvent e) {

}
 private void gameOver(Graphics g) {
      // GAME OVER TEXT
      g.setColor(Color.red);
      g.setFont(new Font("Ink Free", Font.BOLD, 75));
      FontMetrics metrics = getFontMetrics(g.getFont());
      g.drawString("Game Over", (750 - metrics.stringWidth("Game Over")) / 2, 480 / 2);

      // DRAW SCORE AFTER GAME OVER
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 60));
      FontMetrics metrics1 = getFontMetrics(g.getFont());
      g.drawString("Score: " + score, (750) - metrics1.stringWidth("Score: " + score) - 350, 340);

      // DRAW SPACEBAR TEXT AFTER GAME OVER
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 30));
      FontMetrics metrics2 = getFontMetrics(g.getFont());
      g.drawString("Press Space to Restart Game",(750)-metrics2.stringWidth("Press Space to Restart Game ")-150, 450);

      // DRAW HIGH SCORE
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 20));
      FontMetrics metrics3 = getFontMetrics(g.getFont());
     
      // DISPLAYING HIGH SCORE FOR EACH LEVEL
      g.drawString("High Score - EASY: " + highScoreEasy, 430, 300);
      g.drawString("High Score - MEDIUM: " + highScoreMedium, 430, 330);
      g.drawString("High Score - HARD: " + highScoreHard, 430, 360);
 }

     @Override
     public void actionPerformed(ActionEvent e) {
           if (gameStarted && !gameOver) {
              moveSnake();
              checkCollision();
              repaint();
           }
    }


}































public void create_hard_levelPanel(){
    // CREATION OF PANEL
         gamePanelHard = new GamePanelHard();
         gamePanelHard.setSize(800, 600); // Match the size of the game frame);
         gamePanelHard.setBackground(Color.darkGray); // sets the panel background color
         gamePanelHard.setLayout(null); // Use null layout
         setLocationRelativeTo(null); // Center the frame
         setFocusable(true);  //sets the focus on the easy panel
         setFocusTraversalKeysEnabled(true);
    
    // ADDING PANEL TO CARD LAYOUT
         levelCards.add(gamePanelHard, "GamePanelHard");
         gamePanelHard.setVisible(false); // Initially, hide the "Easy" panel
         gamePanelHard.addKeyListener(gamePanelHard);

    
}
      
public class GamePanelHard extends JPanel implements ActionListener, KeyListener {
 
      public ImageIcon block = new ImageIcon(getClass().getResource("block.png"));
    //CREATING AN ARRAY LIST FOR THE BLOCK
    public List<Block> blocks = new ArrayList<>();

    public ImageIcon vine = new ImageIcon(getClass().getResource("vines.png"));
    //CREATING AN ARRAY LIST FOR THE VINES
    public List<Vine> vines = new ArrayList<>();

    private ImageIcon bomb = new ImageIcon(getClass().getResource("bomb.png"));
    //CREATING AN ARRAY LIST FOR THE BOMBS
    private List<Bomb> bombs = new ArrayList<>();

    
class Block {
    int x;
    int y;
    int size;
    private ImageIcon blockImage;

    public Block(int x, int y, int size, ImageIcon blockImage) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.blockImage = blockImage;
    }

    public ImageIcon getImage() {
        return blockImage;
    }
}

class Vine {
    int x;
    int y;
    int width; // Width of the vine
    int height; // Height of the vine
    private ImageIcon vineImage;

    public Vine(int x, int y, int width, int height, ImageIcon vineImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vineImage = vineImage;
    }

    public ImageIcon getImage() {
        return vineImage;
    }
}

class Bomb {
    int x;
    int y;
    int size;
    private ImageIcon bombImage;

    public Bomb(int x, int y, int size, ImageIcon bombImage) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.bombImage = bombImage;
    }

    public ImageIcon getImage() {
        return bombImage;
    }
}

private Timer timer;

   public GamePanelHard() {
          // Set the timer delay to 100 (milliseconds)
          // INITIALIZING TIMER
          timer = new Timer(hardDelay, this);

          // ADDING AND CREATING KEY LISTENER
          addKeyListener(this);
          setFocusable(true);
          setFocusTraversalKeysEnabled(true);
  
          // SETTING UP PROPERTIES OF SNAKE
          snakexlength = new ArrayList<>();
          snakeylength = new ArrayList<>();
  
          // STARTING THE GAME
          startGame();
  
          // LOAD HIGH SCORE FROM PREFERENCES
          prefs = Preferences.userNodeForPackage(finalgame.class);
    }
 

    private void startGame() {
        gameOver = false; // Set game over state to false
        gameStarted = true;
        initializeSnake();
        generateRandomPosition();
        generateRandomBlocks(); // Add this line to generate blocks,bombs and vines.
        timer.start(); // Start the timer
    }
    
   
    public void initializeSnake() {
        // Clear any previous data if needed
        snakexlength.clear();
        snakeylength.clear();
        
        // Set the initial position of the snake's head randomly within the black area
        Random rand = new Random();
        
        int panelWidth = 800; // Specify the panel width
        int panelHeight = 600; // Specify the panel height
    
        int initialX;
        int initialY;
    
        // Generate initial positions that don't collide with the snake's body
        do {
            initialX = 25 + rand.nextInt((panelWidth - 50) / 25) * 25; // Random X position within the black area
            initialY = 100 + rand.nextInt((panelHeight - 125) / 25) * 25; // Random Y position within the black area
        } while (snakexlength.contains(initialX) || snakeylength.contains(initialY));
    
        // Set the initial position of the snake's head
        snakexlength.add(initialX);
        snakeylength.add(initialY);
    
        // Set initial direction of the snake (you can modify this based on your logic)
        left = false;
        right = true;
        up = false;
        down = false;
    
        // Set initial positions of the snake's body parts (adjust as needed)
        for (int i = 1; i < lengthsnake; i++) {
            if (right) {
                snakexlength.add(initialX - i * 25); // Adjust as needed
                snakeylength.add(initialY);
            } else if (left) {
                snakexlength.add(initialX + i * 25); // Adjust as needed
                snakeylength.add(initialY);
            } else if (up) {
                snakexlength.add(initialX);
                snakeylength.add(initialY + i * 25); // Adjust as needed
            } else if (down) {
                snakexlength.add(initialX);
                snakeylength.add(initialY - i * 25); // Adjust as needed
            }
        }
    }
    
 private void generateRandomPosition() {
        Random rand = new Random();
        int maxX = 750 - 25; // Maximum X position for enemy within the board size (right - left)
        int maxY = 480 - 25; // Maximum Y position for enemy within the board size (top - down)
    
        // Define a list to store the positions of random blocks
        List<Point> blockPositions = new ArrayList<>();
        for (Block block : blocks) {
            blockPositions.add(new Point(block.x, block.y));
        }
    
        do {
            enemyXPos = 25 + rand.nextInt(maxX / 25) * 25; // Random X position within the board size
            enemyYPos = 100 + rand.nextInt(maxY / 25) * 25; // Random Y position within the board size
        } while (blockPositions.contains(new Point(enemyXPos, enemyYPos)));
    
        // Now, enemyXPos and enemyYPos contain a position that doesn't overlap with the blocks
    }


// OVERRIDE A PAINT METHOD TO DRAW COMPONENTS ON JPANEL    
 @Override
 protected void paintComponent(Graphics g) {
     super.paintComponent(g);
        
     // Your drawing code here (same as your previous paint method)
              g.setColor(Color.white);// Border Color is set as White
              g.drawRect(15, 10, 755, 60);// Rectangle Border for title
              g.drawRect(15, 80, 755, 470);// Rectangle border for Game
              snaketitle.paintIcon(gameFrame, g, 18, 14);// adds image for title here x is space from left and y is space from top
              g.setColor(Color.black);// Sets the color as black
              g.fillRect(19, 83, 749, 464);// fill Rectangle border for Game as Black

     // DRAWING SNAKE HEAD AND BODY
              drawSnake(g);

     // DRAWING ENEMY
              enemy.paintIcon(gameFrame, g, enemyXPos, enemyYPos);
     
     // DRAWING THE TROPHY
              g.drawImage(trophy.getImage(), 550, 30, this); // Adjust the coordinates as needed                

     // DRAW SCORE
              g.setColor(Color.white);
              g.setFont(new Font("Ink Free", Font.BOLD, 20));
              g.drawString("Score: " + score, 68, 50);
              g.setColor(Color.white);
              g.setFont(new Font("Ink Free", Font.BOLD, 20));
              g.drawString("High Score: Hard " + highScoreHard, 580, 50);

     // DRAW GAME OVER SCREEN
              if (gameOver) {
               gameOver(g);
               timer.stop(); // Stop the timer
             }

     // DRAW BLOCKS VINES AND BOMBS
            paintBlocks(g);        
     // DISPOSING GRAPHICS
              g.dispose();
}
 
 public void drawSnake(Graphics g) {
         // DRAWING HEAD
             if (left) {
               leftmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (right) {
               rightmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (up) {
               upmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             } else if (down) {
               downmouth.paintIcon(gameFrame, g, snakexlength.get(0), snakeylength.get(0));
             }

             // DRAWING BODY
             for (int i = 1; i < lengthsnake; i++) {
             snakeimage.paintIcon(gameFrame, g, snakexlength.get(i), snakeylength.get(i));
             }
 }   

  public void paintBlocks(Graphics g) {
    for (Block block : blocks) {
        int blockSize = block.size;
        ImageIcon blockImage = block.getImage();
        if (blockImage != null) {
            g.drawImage(blockImage.getImage(), block.x, block.y, blockSize, blockSize, gamePanelHard);
        } else {
            System.err.println("Block image is null!");
        }
    }

    for (Vine vine : vines) {
        int vineWidth = vine.width;
        int vineHeight = vine.height;
        ImageIcon vineImage = vine.getImage();
        if (vineImage != null) {
            g.drawImage(vineImage.getImage(), vine.x, vine.y, vineWidth, vineHeight, gamePanelHard);
        } else {
            System.err.println("Vine image is null!");
        }
    }

    for (Bomb bomb : bombs) {
        int bombSize = bomb.size;
        ImageIcon bombImage = bomb.getImage();
        if (bombImage != null) {
            g.drawImage(bombImage.getImage(), bomb.x, bomb.y, bombSize, bombSize, gamePanelHard);
        } else {
            System.err.println("Bomb image is null!");
        }
    }

}

private void generateRandomBlocks() {
    Random rand = new Random();
    blocks.clear(); // Clear existing blocks
    vines.clear();  // Clear existing vines
    bombs.clear();  // Clear existing bombs

     int maxLines = 3; // Maximum number of lines of blocks

    int maxX = (750 - 25) / 25 * 25; // Adjust the maximum X position if needed
    int maxY = (480 - 25) / 25 * 25; // Adjust the maximum Y position if needed

    int minLineSize = 3; // Minimum number of blocks in a line
    int maxLineSize = 5; // Maximum number of blocks in a line

    int blockSize = 25; // Size of each block

    for (int line = 0; line < maxLines; line++) {
        // Randomly determine the orientation of the line (horizontal or vertical)
        boolean isHorizontal = rand.nextBoolean();

        // Randomly determine the starting position of the line within the black area
        int startX = 25 + rand.nextInt(maxX / 25) * 25;
        int startY = 100 + rand.nextInt(maxY / 25) * 25;

        // Generate a random line size within the specified range
        int lineSize = minLineSize + rand.nextInt(maxLineSize - minLineSize + 1);

        // Calculate the size of the entire line
        int lineSizePixels = lineSize * blockSize;

        // Adjust the starting position to ensure the entire line fits within the bounds
        if (isHorizontal) {
            startX = Math.min(startX, 750 - lineSizePixels);
        } else {
            startY = Math.min(startY, 480 - lineSizePixels);
        }

        // Generate a line of blocks with the same size
        for (int i = 0; i < lineSize; i++) {
            int blockX = startX + (isHorizontal ? i * blockSize : 0);
            int blockY = startY + (isHorizontal ? 0 : i * blockSize);

            blocks.add(new Block(blockX, blockY, blockSize, block));
        }
    }

    // Generate random vines
    int numVines = rand.nextInt(3) + 1; // Number of vines (1 to 3)

    for (int i = 0; i < numVines; i++) {
        int vineX = 25 + rand.nextInt(maxX / 25) * 25;
        int vineY = 100 + rand.nextInt(maxY / 25) * 25;

        // Adjust vine size to 25x90 pixels
        int vineWidth = 25;
        int vineHeight = 140;
        vines.add(new Vine(vineX, vineY, vineWidth, vineHeight, vine));
    }

     // Generate exactly 6 bomb images
     int bombSize = 25; // Adjust bomb size as needed (e.g., 25x25 pixels)
     while (bombs.size() < 3) {
               int bombX = 25 + rand.nextInt(maxX / 25) * 25;
               int bombY = 100 + rand.nextInt(maxY / 25) * 25;

            Bomb newBomb = new Bomb(bombX, bombY, bombSize, bomb);
            // Check if the new bomb's position conflicts with existing bombs or the enemy
               boolean conflict = false;
               for (Bomb existingBomb : bombs) {
                    if (bombX == existingBomb.x && bombY == existingBomb.y) {
                    conflict = true;
                    break;
                   }
              }

            // Check if the new bomb's position conflicts with the enemy
              if (bombX == enemyXPos && bombY == enemyYPos) {
              conflict = true;
            }

            // Add the new bomb if there's no conflict
             if (!conflict) {
              bombs.add(newBomb);
             }
       }
}

 private void moveSnake() {
       // MOVING THE BODY OF SNAKE
          if (!gameOver) {// CHECKS IF GAME IS OVER OR NOT
              for (int i = lengthsnake - 1; i > 0; i--) {// last part of snake is moved
              snakexlength.set(i, snakexlength.get(i - 1));// adding the last part in the current position of snake
              snakeylength.set(i, snakeylength.get(i - 1));
             }

       // MOVE THE HEAD OF SNAKE BASED ON DIRECTION //CHANGES POSITION
          if (left) {
              snakexlength.set(0, snakexlength.get(0) - 25);
          } else if (right) {
              snakexlength.set(0, snakexlength.get(0) + 25);
          } else if (up) {
              snakeylength.set(0, snakeylength.get(0) - 25);
          } else if (down) {
              snakeylength.set(0, snakeylength.get(0) + 25);
          }
         
         
         // Check if the snake hits the boundaries of the black area
        if (snakexlength.get(0) < 15 || snakexlength.get(0) >= 749 || 
        snakeylength.get(0) < 100 || snakeylength.get(0) >= 510) {
        gameOver = true; // Set game over state to true
      }
    
    }
  }

 private void checkCollision() {
      // CHECK IF THE SNAKE HITS THE BOUNDARIES
      if (snakexlength.get(0) >= 750 || snakexlength.get(0) < 25|| snakeylength.get(0) >= 560  ||snakeylength.get(0) < 100) 
           {gameOver = true;} // Set game over state to true if snake hits the boundary

      // CHECK IF THE SNAKE EATS FOOD AND INCREASE SIZE AND SCORE
      if (snakexlength.get(0) == enemyXPos && snakeylength.get(0) == enemyYPos) {
           generateRandomPosition();
           increaseSnakeSize();
      }
      // CHECK IF THE SNAKE EATS ITSELF
      for (int i = 1; i < lengthsnake; i++) {
          if (snakexlength.get(i).equals(snakexlength.get(0)) && snakeylength.get(i).equals(snakeylength.get(0))) {
          // Checking if the body position is the same as the head
          gameOver = true; // Set game over state to true
          break;
       }
     }

       // CHECK IF THE SNAKE HITS A BLOCK
       for (Block block : blocks) {
        if (snakexlength.get(0) == block.x && snakeylength.get(0) == block.y) {
        gameOver = true; // Set game over state to true if snake hits a block
        break;
     }
     }

     // CHECK IF THE SNAKE HITS A BOMB
     for (Bomb bomb : bombs) {
        if (snakexlength.get(0) == bomb.x && snakeylength.get(0) == bomb.y) {
          gameOver = true; // Set game over state to true if snake hits a bomb
        break;
     }
    }
}

 private void increaseSnakeSize() {
   int lastIndex = snakexlength.size() - 1;
   int prevX = snakexlength.get(lastIndex);
   int prevY = snakeylength.get(lastIndex);
   snakexlength.add(prevX);
   snakeylength.add(prevY);
   lengthsnake++;
   score++; // Increase the score by one

  // UPDATE HIGH SCORE BASED ON THE CURRENT LEVEL
  switch (currentLevel) {
    case EASY:
        if (score > highScoreEasy) {
            highScoreEasy = score;
            prefs.putInt("HighScoreEasy", highScoreEasy);
        }
        break;

    case MEDIUM:
        if (score > highScoreMedium) {
            highScoreMedium = score;
            prefs.putInt("HighScoreMedium", highScoreMedium);
        }
        break;

    case HARD:
        if (score > highScoreHard) {
            highScoreHard = score;
            prefs.putInt("HighScoreHard", highScoreHard);
        }
        break;

    default:
        break;
}
}

// CONTROLLING SNAKE BY PRESSING KEYS
@Override
public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
 
    if (!gameStarted) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
             cardLayout.show(levelCards, "LevelPanel"); // Switch to the "LevelPanel" card
             gamePanelHard.setVisible(false); // Hide the game panel
             levelPanel.setVisible(true); // Show the level selection panel
             levelPanel.requestFocus(); // Set focus to the game panel
        }
        return; // Ignore other keys if the game hasn't started yet
    }
  if  (e.getKeyCode() == KeyEvent.VK_LEFT && !right) { // ie checking that if left key is pressed and the snake is
                                                    // not moving in the right
     left = true;
     right = false;
     up = false;
     down = false;
 }
 else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !left) { // ie checking that if right key is pressed and the snake is
                                                    // not moving in the left
     left = false;
     right = true;
     up = false;
     down = false;
 }
 else if (e.getKeyCode() == KeyEvent.VK_UP && !down) { // ie checking that if up key is pressed and the snake is not
                                                 // moving in the down
     left = false;
     right = false;
     up = true;
     down = false;
 }
 else if  (e.getKeyCode() == KeyEvent.VK_DOWN && !up) { // ie checking that if down key is pressed and the snake is not
                                                 // moving in the up
     left = false;
     right = false;
     up = false;
     down = true;
 }
 else if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
    finalgame.this.resetGame(timer); // Restart the game and show level selection
     }               
 }

@Override
public void keyTyped(KeyEvent e) {

}

@Override
public void keyReleased(KeyEvent e) {

}
 private void gameOver(Graphics g) {
      // GAME OVER TEXT
      g.setColor(Color.red);
      g.setFont(new Font("Ink Free", Font.BOLD, 75));
      FontMetrics metrics = getFontMetrics(g.getFont());
      g.drawString("Game Over", (750 - metrics.stringWidth("Game Over")) / 2, 480 / 2);

      // DRAW SCORE AFTER GAME OVER
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 60));
      FontMetrics metrics1 = getFontMetrics(g.getFont());
      g.drawString("Score: " + score, (750) - metrics1.stringWidth("Score: " + score) - 350, 340);

      // DRAW SPACEBAR TEXT AFTER GAME OVER
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 30));
      FontMetrics metrics2 = getFontMetrics(g.getFont());
      g.drawString("Press Space to Restart Game",(750)-metrics2.stringWidth("Press Space to Restart Game ")-150, 450);

      // DRAW HIGH SCORE
      g.setColor(Color.white);
      g.setFont(new Font("Ink Free", Font.BOLD, 20));
      FontMetrics metrics3 = getFontMetrics(g.getFont());
      
      //DISPLAY HIGH SCORE FOR EACH LEVEL
      g.drawString("High Score - EASY: " + highScoreEasy, 430, 300);
      g.drawString("High Score - MEDIUM: " + highScoreMedium, 430, 330);
      g.drawString("High Score - HARD: " + highScoreHard, 430, 360);
 }

     @Override
     public void actionPerformed(ActionEvent e) {
           if (gameStarted && !gameOver) {
              moveSnake();
              checkCollision();
              repaint();
           }
    }

}







public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            new finalgame();
           
            
        }
    });
}


}
