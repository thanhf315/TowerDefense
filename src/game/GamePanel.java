package game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener {
    private static final long serialVersionUID = -266426690684141363L;

    private GameField enclosingGame;
    public int mouseX;				
    public int mouseY;			
    public boolean mouseIsPressed;

    public GamePanel (GameField enclosingGame) {
    	this.addMouseListener(this);
    	this.addMouseMotionListener(this);
        this.enclosingGame = enclosingGame;
    }


	public void paintComponent (Graphics g)
    {
        enclosingGame.draw (g);
    }
    
    public Coordinate getCoordinate()
    {
    	return new Coordinate(mouseX, mouseY);
    }
    
    public Dimension getMinimumSize ()
    {
        return new Dimension(1700,800);
    }
    public Dimension getMaximumSize ()
    {
        return new Dimension(1700,800);
    }
    public Dimension getPreferredSize ()
    {
        return new Dimension(1700,800);
    }

	public void mouseClicked(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = true;
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		mouseIsPressed = false;

	}
}
