
package game.snake.canvas;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

public class snakeCanvas extends Canvas{
    
    private final int BOX_HEIGHT = 5;
    private final int BOX_WIDTH = 5;
    private final int GRID_HEIGHT = 30; // how many boxes are in grid
    private final int GRID_WIDTH = 30; // how many boxes are in grid
    private final int GRID_WIDTH_RATIO = GRID_WIDTH * BOX_WIDTH; //
    private final int GRID_HEIGHT_RATIO = GRID_HEIGHT * BOX_HEIGHT;
    
    private  LinkedList<Point> snake; // point is class in java
    
    
    public void Draw(Graphics g) {
    }
    
    
    /**
     * 
     * @param g Graphics
     */
    public void DrawGrid(Graphics g){
        
        // drawing an outside rect
        g.drawRect(0, 0, GRID_WIDTH_RATIO, GRID_HEIGHT_RATIO); // starts with zero zero, how big 
        
        // drawing the vertical lines
        for (int x = BOX_WIDTH; x < GRID_WIDTH_RATIO; x+=BOX_WIDTH) { 
            g.drawLine(x, 0, x, GRID_HEIGHT_RATIO);
        }
        
        //drawing the horizontal lines
        for (int y = BOX_HEIGHT; y < GRID_HEIGHT_RATIO; y+=BOX_HEIGHT) { 
            g.drawLine(0, y, GRID_WIDTH_RATIO, y);
        }
        
    }
}
