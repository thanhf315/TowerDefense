package game;

import java.awt.Graphics;
import java.awt.Image;

abstract public class Tower implements GameEntity, GameTile {
	protected Coordinate position;	
	protected Image tower; 			
	protected int width, height, range;
	protected double timeSinceLastFire;
	
	public void draw(Graphics g)
	{
		g.drawImage(tower, position.getX() , position.getY() , null);
	}
	
	public void setPosition(Coordinate c) {
		position = c;
	}
	
	abstract void interact(GameField game, double deltaTime);
}





