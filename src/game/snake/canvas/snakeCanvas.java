package game.snake.canvas;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class snakeCanvas extends Canvas implements Runnable, KeyListener {

    private final int BOX_HEIGHT = 5;
    private final int BOX_WIDTH = 5;
    private final int GRID_HEIGHT = 50; // how many boxes are in grid
    private final int GRID_WIDTH = 50; // how many boxes are in grid
    private final int GRID_WIDTH_RATIO = GRID_WIDTH * BOX_WIDTH; //
    private final int GRID_HEIGHT_RATIO = GRID_HEIGHT * BOX_HEIGHT;

    private LinkedList<Point> snake; // point is class in java
    private Point fruit;

    private Thread runThread; // multitask run
    private Graphics globalGraphics;
    
    private int direction = Direction.NO_DIRECTION;

    
    
    public void init() {
    }
    
    /**
     * 
     * @param g Graphics
     */
    public void paint(Graphics g) {

        
        this.setPreferredSize(new Dimension(640, 480));
        
        snake = new LinkedList<Point>();
        snake.add(new Point(3,1));
        snake.add(new Point(3,2));
        snake.add(new Point(3,3));
        
        fruit = new Point(0,0);
        globalGraphics = g.create();
        this.addKeyListener(this);
        
        
        if (runThread == null) {
            runThread = new Thread(this);
            runThread.start();
        }
    }
    
    /**
     * 
     * @param g Graphics
     */
    public void Draw(Graphics g) {
        g.clearRect(0, 0, GRID_WIDTH_RATIO, GRID_HEIGHT_RATIO); // removes snake path
        DrawGrid(g);
        DrawSnake(g);
        DrawFruit(g);
    }

    
    public void Move() {
        // logic
        Point head = snake.peekFirst(); // setting first point of linkedlist
        Point newPoint = head;
        switch(direction) {
            case Direction.NORTH:
                newPoint = new Point(head.x, head.y -1);
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

        snake.remove(snake.peekLast()); // last dot
        
        if(newPoint.equals(fruit)) {
            // tha snake has hit fruit
        } else if (newPoint.x < 0 || newPoint.x > GRID_WIDTH) {
            // we went out, reset game
        } else if (newPoint.y < 0 || newPoint.y > GRID_HEIGHT) {
            // we went out, reset game
        } else if (snake.contains(newPoint)) {
            // we ran into ourselves, reset game
        }

       // if we reach this point in code, we're still good
        snake.push(newPoint);
        
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

    @Override
    public void run() {
        while (true) {
            /// runs indefinitely
            Move();
            Draw(globalGraphics);
            try {

                Thread.currentThread();
                Thread.sleep(100);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        
        switch(ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                direction = Direction.NORTH;
                break;
            case KeyEvent.VK_DOWN:
                direction = Direction.SOUTH;
                break;
            case KeyEvent.VK_RIGHT:
                direction = Direction.EAST;
                break;
            case KeyEvent.VK_LEFT:
                direction = Direction.WEST;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
