package game.snake.canvas;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JOptionPane;

public class snakeCanvas extends Canvas implements Runnable, KeyListener {

    private final int BOX_HEIGHT = 15;
    private final int BOX_WIDTH = 15;
    private final int GRID_HEIGHT = 25; // how many boxes are in grid
    private final int GRID_WIDTH = 25; // how many boxes are in grid
    private final int GRID_WIDTH_RATIO = GRID_WIDTH * BOX_WIDTH; //
    private final int GRID_HEIGHT_RATIO = GRID_HEIGHT * BOX_HEIGHT;

    private LinkedList<Point> snake; // point is class in java
    private Point fruit;

    private Thread runThread; // multitask run

    private int direction = Direction.NO_DIRECTION;

    private int score = 0;
    private String highScore = "";

    private Image menuImage;

    private boolean isInMenu = true;
    private boolean isAtEndGame = false;
    private boolean won = false;

    public void init() {
    }

    /**
     *
     * @param g Graphics
     */
    public void paint(Graphics g) {

        if (runThread == null) {
            this.setPreferredSize(new Dimension(640, 480));
            this.addKeyListener(this);
            runThread = new Thread(this);
            runThread.start();
        }

        if (isInMenu) {
            // draw menu
            DrawMenu(g);
        } else if (isAtEndGame) {
            // draw end game
            DrawEndGame(g);
        } else {
            // draw everythin else
            if (snake == null) {
                snake = new LinkedList<Point>();
                GenerateDefaultSnake();
                PlaceFruit();
            }

            if (highScore.equals("")) {
                // init highscores
                highScore = this.getHighScore();

            }

            DrawFruit(g);
            DrawGrid(g);
            DrawSnake(g);
            DrawScore(g);

        }

    }

    public void DrawEndGame(Graphics g) {
        
        BufferedImage endGameImage = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics endGameGraphics = endGameImage.getGraphics();
        endGameGraphics.setColor(Color.BLACK);
        
        if (won) {
            endGameGraphics.drawString("You spent too long playing this game!", this.getPreferredSize().width / 2, this.getPreferredSize().height / 2);
        } else {
            endGameGraphics.drawString("You lost. Boo hoo.", this.getPreferredSize().width / 2, this.getPreferredSize().height / 2);
        }

        endGameGraphics.drawString("Your score: ", this.getPreferredSize().width / 2, (this.getPreferredSize().height / 2) + 20);
        endGameGraphics.drawString("Press \"space\" to start a new game!", this.getPreferredSize().width / 2, (this.getPreferredSize().height / 2) + 40);

        g.drawImage(endGameImage, 0, 0, this);

    }

    public void DrawMenu(Graphics g) {
        if (this.menuImage == null) {

            try {
                URL imagePath = snakeCanvas.class.getResource("homescreen.png");
                this.menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
            } catch (Exception e) {
                // image does not exist
                e.printStackTrace();
            }

        }

        g.drawImage(menuImage, 0, 0, 640, 480, this);
    }

    public void GenerateDefaultSnake() {
        score = 0;
        snake.clear();
        snake.add(new Point(0, 2));
        snake.add(new Point(0, 1));
        snake.add(new Point(0, 0));
        direction = Direction.NO_DIRECTION;
    }

    public void update(Graphics g) {

        // this is the default update method which will contain our double buffering
        Graphics offScreenGraphics; // these are the graphics we will use to draw offscreen
        BufferedImage offScreen = null;
        Dimension d = this.getSize();

        offScreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        offScreenGraphics = offScreen.getGraphics();
        offScreenGraphics.setColor(this.getBackground());
        offScreenGraphics.fillRect(0, 0, d.width, d.height);
        offScreenGraphics.setColor(this.getForeground());

        paint(offScreenGraphics);

        //flip
        g.drawImage(offScreen, 0, 0, this);

    }

    public void Move() {
        // logic
        
        if(direction == Direction.NO_DIRECTION) {
            return;
        }
        
        Point head = snake.peekFirst(); // setting first point of linkedlist
        Point newPoint = head;
        switch (direction) {
            case Direction.NORTH:
                newPoint = new Point(head.x, head.y - 1);
                break;
            case Direction.SOUTH:
                newPoint = new Point(head.x, head.y + 1);
                break;
            case Direction.WEST:
                newPoint = new Point(head.x - 1, head.y);
                break;
            case Direction.EAST:
                newPoint = new Point(head.x + 1, head.y);
                break;
        }
        if (this.direction != Direction.NO_DIRECTION) {
            snake.remove(snake.peekLast()); // last dot
        }
        if (newPoint.equals(fruit)) {
            // tha snake has hit fruit
            score += 10;
            Point addPoint = (Point) newPoint.clone();
            switch (direction) {
                case Direction.NORTH:
                    newPoint = new Point(head.x, head.y - 1);
                    break;
                case Direction.SOUTH:
                    newPoint = new Point(head.x, head.y + 1);
                    break;
                case Direction.WEST:
                    newPoint = new Point(head.x - 1, head.y);
                    break;
                case Direction.EAST:
                    newPoint = new Point(head.x + 1, head.y);
                    break;
            }
            snake.push(addPoint);
            PlaceFruit();

        } else if (newPoint.x < 0 || newPoint.x > (GRID_WIDTH - 1)) {
            // we went out, reset game
            CheckScore();
            won = false;
            isAtEndGame = true;
            return;
        } else if (newPoint.y < 0 || newPoint.y > (GRID_HEIGHT - 1)) {
            // we went out, reset game
            CheckScore();
            won = false;
            isAtEndGame = true;
            return;

        } else if (snake.contains(newPoint)) {
            // we ran into ourselves, reset game
            
            CheckScore();
            won = false;
            isAtEndGame = true;
            return;

        } else if (snake.size() == (GRID_WIDTH * GRID_HEIGHT)) {
            // we won!
            CheckScore();
            won = true;
            isAtEndGame = true;
        }

        // if we reach this point in code, we're still good
        snake.push(newPoint);

    }

    public void DrawScore(Graphics g) {
        g.drawString("Score: " + score, 0, GRID_HEIGHT_RATIO + 10);
        g.drawString("Hightscore: " + highScore, 0, GRID_HEIGHT_RATIO + 20);
    }

    public void CheckScore() {
        // user has set a new record
        if (highScore.equals("")) {
            return;
        }

        if (score > Integer.parseInt(highScore.split(":")[1])) {
            String name = JOptionPane.showInputDialog("You set a new highscore. What is your name?");
            highScore = name + ":" + score;

            File scoreFile = new File("highscore.dat");
            if (!scoreFile.exists()) {
                try {
                    scoreFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
            FileWriter writeFile = null;
            BufferedWriter writer = null;

            try {
                writeFile = new FileWriter(scoreFile);
                writer = new BufferedWriter(writeFile);
                writer.write(this.highScore);
            } catch (Exception e) {
                // errors

            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     *
     * @param g Graphics
     */
    public void DrawGrid(Graphics g) {

        // drawing an outside rect
        g.drawRect(0, 0, GRID_WIDTH_RATIO, GRID_HEIGHT_RATIO); // starts with zero zero, how big 

        // drawing the vertical lines
        for (int x = BOX_WIDTH; x < GRID_WIDTH_RATIO; x += BOX_WIDTH) {
            g.drawLine(x, 0, x, GRID_HEIGHT_RATIO);
        }

        //drawing the horizontal lines
        for (int y = BOX_HEIGHT; y < GRID_HEIGHT_RATIO; y += BOX_HEIGHT) {
            g.drawLine(0, y, GRID_WIDTH_RATIO, y);
        }

    }

    /**
     *
     * @param g Graphics
     */
    public void DrawSnake(Graphics g) {
        g.setColor(Color.GREEN);

        for (Point p : snake) {
            g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        }
        g.setColor(Color.BLACK);

    }

    /**
     *
     * @param g Graphics
     */
    public void DrawFruit(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        g.setColor(Color.BLACK);
    }

    public void PlaceFruit() {
        Random rand = new Random();

        int randomX = rand.nextInt(GRID_WIDTH);
        int randomY = rand.nextInt(GRID_HEIGHT);
        Point randomPoint = new Point(randomX, randomY);
        while (snake.contains(randomPoint)) {
            randomX = rand.nextInt(GRID_WIDTH);
            randomY = rand.nextInt(GRID_HEIGHT);
            randomPoint = new Point(randomX, randomY);

        }
        fruit = randomPoint;

    }

    @Override
    public void run() {
        while (true) {
            /// runs indefinitely
            repaint();
            if (!isInMenu && !isAtEndGame) {
                Move();
            }

            try {

                Thread.currentThread();
                Thread.sleep(100);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getHighScore() {
        // format: User: 100
        FileReader readFile = null;
        BufferedReader reader = null;

        try {
            readFile = new FileReader("highscore.dat");
            reader = new BufferedReader(readFile);
            return reader.readLine();

        } catch (Exception e) {
            return "Nobody: 0";
        } finally {
            try {
                // executes after finish
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent ke) {

        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (direction != Direction.SOUTH) {
                    direction = Direction.NORTH;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != Direction.NORTH) {
                    direction = Direction.SOUTH;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != Direction.WEST) {
                    direction = Direction.EAST;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (direction != Direction.EAST) {
                    direction = Direction.WEST;
                }
                break;
            case KeyEvent.VK_ENTER:
                if (isInMenu) {
                    isInMenu = false;
                    repaint();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                isInMenu = true;
                break;
            case KeyEvent.VK_SPACE:
                if (isAtEndGame) {
                    isAtEndGame = false;
                    won = false;
                    GenerateDefaultSnake();
                    repaint();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
