package com.argeslab.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import com.argeslab.main.Game;
import com.argeslab.main.Sound;
import com.argeslab.world.Camera;
import com.argeslab.world.World;

public class Player extends Entity {
	
	public boolean right, up, left, down;
	public String direction = "RIGHT";
	public boolean caughtByEnemy = false;
	
	// Animação
	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 1;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;

	public Player(double x, double y, int width, int height, double speed, BufferedImage sprite) {
		super(x, y, width, height, speed, sprite);
		
		rightPlayer = new BufferedImage[2];
		leftPlayer = new BufferedImage[2];
		
		for(int i = 0; i < rightPlayer.length; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
		}
		
		for(int i = 0; i < leftPlayer.length; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
		}
	}
	

	public void tick() {
		depth = 1;
		
		frames++;
		
		if(frames == maxFrames) {
			frames = 0;
			index++;
			
			if(index > maxIndex) {
				index = 0;
			}
		}
		
		if(right && World.isFree((int)(x + speed), this.getY())) {
			x+= speed;
			direction = "RIGHT";
		} else if(left && World.isFree((int)(x - speed), this.getY())) {
			x-= speed;
			direction = "LEFT";
		}
		
		if(up && World.isFree(this.getX(), (int)(y - speed))) {
			y-= speed;
		}else if(down && World.isFree(this.getX(), (int)(y + speed))) {
			y+= speed;
		}
		
		checkColisions();
		
		if(Game.fruitCount == 0 && Game.seedCount == 0) {
			Game.venceu = true;
			Sound.musicEffect.play();
			World.restartGame();
		}
	}
	
	public void checkColisions() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity current = Game.entities.get(i);
			
			if(current instanceof Fruit) {
				if(Entity.isColliding(this, current)) {
					Sound.eatFruitEffect.play();
					Game.entities.remove(i);
					Game.fruitCount--;
					Game.ghostMode = true;
					return;
				}
			}
			
			if(current instanceof Seed) {
				if(Entity.isColliding(this, current)) {
					Sound.eatEffect.play();
					Game.entities.remove(i);
					Game.seedCount--;
					return;
				}
			}
			
			if(current instanceof Enemy) {
				if(Entity.isColliding(this, current)) {
					if(!Game.ghostMode) {
						Sound.deathEffect.play();
						Game.gameState = "GAMEOVER";
						Game.venceu = false;
						World.restartGame();
					}
					
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		switch (direction) {
			case "RIGHT": {
				g.drawImage(rightPlayer[index], (int)this.getX() - Camera.x, (int)this.getY() - Camera.y, null);
				break;
			}
			
			case "LEFT": {
				g.drawImage(leftPlayer[index], (int)this.getX() - Camera.x, (int)this.getY() - Camera.y, null);
				break;
			}
		}		
	}
}
