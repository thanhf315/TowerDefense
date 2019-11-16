package game;

public class SniperBullet extends Bullet {
	public SniperBullet(Coordinate pos, Coordinate target) {
		ImageLoader loader = ImageLoader.getLoader();
		this.picture = loader.getImage("resources/SniperBullet.png");
		this.posX = pos.x;
		this.posY = pos.y;		
		this.velocityX = (target.x - this.posX)/2;
		this.velocityY = (target.y - this.posY)/2;
		this.ageInSeconds = 0;
		this.strength = 50;
	}	
}
