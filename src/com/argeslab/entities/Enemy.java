package com.argeslab.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import com.argeslab.main.Game;
import com.argeslab.world.AStar;
import com.argeslab.world.Camera;
import com.argeslab.world.Vector2i;

public class Enemy extends Entity {
	
	public double xAtual;
	public int ghostFrames = 0;
	
	// Animação
	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 1;
	private BufferedImage[] rightEnemy;
	private BufferedImage[] leftEnemy;
	private BufferedImage[] rightEnemyGhost;
	private BufferedImage[] leftEnemyGhost;

	public Enemy(double x, double y, int width, int height, double speed, BufferedImage sprite) {
		super(x, y, width, height, speed, sprite);
		xAtual = x;
		
		rightEnemy = new BufferedImage[2];
		leftEnemy = new BufferedImage[2];
		
		for(int i = 0; i < rightEnemy.length; i++) {
			rightEnemy[i] = Game.spritesheet.getSprite(64 + (i * 16), 0, 16, 16);
		}
		
		for(int i = 0; i < leftEnemy.length; i++) {
			leftEnemy[i] = Game.spritesheet.getSprite(64 + (i * 16), 16, 16, 16);
		}
		
		rightEnemyGhost = new BufferedImage[2];
		leftEnemyGhost = new BufferedImage[2];
		
		for(int i = 0; i < rightEnemyGhost.length; i++) {
			rightEnemyGhost[i] = Game.spritesheet.getSprite(96 + (i * 16), 0, 16, 16);
		}
		
		for(int i = 0; i < leftEnemyGhost.length; i++) {
			leftEnemyGhost[i] = Game.spritesheet.getSprite(96 + (i * 16), 16, 16, 16);
		}
	}

	public void tick() {
		depth = 0;
		
		frames++;
		
		if(frames == maxFrames) {
			frames = 0;
			index++;
			
			if(index > maxIndex) {
				index = 0;
			}
		}
		
		if(!Game.ghostMode) {
			ghostFrames = 0;
			
			if(path == null || path.size() == 0) {
				Vector2i start = new Vector2i((int)(x / 16), (int)(y / 16));
				Vector2i end = new Vector2i((int)(Game.player.x / 16), (int)(Game.player.y / 16));
				path = AStar.findPath(Game.world, start, end);
			}
			
			/*if(new Random().nextInt(100) < 50) {
				followPath(path);
			}*/
			
			followPath(path);
			
			if(x % 16 == 0 && y % 16 == 0) {
				Vector2i start = new Vector2i((int)(x / 16), (int)(y / 16));
				Vector2i end = new Vector2i((int)(Game.player.x / 16), (int)(Game.player.y / 16));
				path = AStar.findPath(Game.world, start, end);
			}
		} else {
			ghostFrames++;
			
			if(ghostFrames == 60 * 4) {
				Game.ghostMode = false;
			}
		}
	}
	
	
	public void render(Graphics g) {
		if(xAtual > this.getX()) {
			if(Game.ghostMode) {
				g.drawImage(rightEnemyGhost[index], (int)this.getX() - Camera.x, (int)this.getY() - Camera.y, null);
			} else {
				g.drawImage(rightEnemy[index], (int)this.getX() - Camera.x, (int)this.getY() - Camera.y, null);
			}
		} else {
			if(Game.ghostMode) {
				g.drawImage(leftEnemyGhost[index], (int)this.getX() - Camera.x, (int)this.getY() - Camera.y, null);
			} else {
				g.drawImage(leftEnemy[index], (int)this.getX() - Camera.x, (int)this.getY() - Camera.y, null);
			}
		}
		
		xAtual = this.getX();
	}
}
