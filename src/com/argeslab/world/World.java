package com.argeslab.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.argeslab.entities.Enemy;
import com.argeslab.entities.Entity;
import com.argeslab.entities.Fruit;
import com.argeslab.entities.Player;
import com.argeslab.entities.Seed;
import com.argeslab.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			int[] pixels = new int[WIDTH * HEIGHT];
			tiles = new Tile[pixels.length];
			
			map.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
			
			for(int xx = 0; xx < WIDTH; xx++) {
				for(int yy = 0; yy < HEIGHT; yy++) {
					int pixelAtual = pixels[xx + (yy * WIDTH)];
					
					// Chão - Tudo é chão, se não for troca depois
					tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
					
					if(pixelAtual == 0xFFFFFFFF) {
						// Parede
						tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL);
					} else if(pixelAtual == 0xFF0026FF) {
						// Player
						Game.player.setX(xx * 16);
						Game.player.setY(yy * 16);
					} else if(pixelAtual == 0xFFFF6A00) {
						// Inimigos
						Enemy enemy = new Enemy(xx * 16, yy * 16, 16, 16, 2, Entity.ENEMY);
						Game.entities.add(enemy);
						
						// Coloca sementes no local dos inimigos também
						Seed seed = new Seed(xx * 16, yy * 16, 16, 16, 0, Entity.SEED_SPRITE);
						Game.entities.add(seed);
						Game.seedCount++;
					} else if(pixelAtual == 0xFFFFD800) {
						// Sementes para o pac man comer
						Seed seed = new Seed(xx * 16, yy * 16, 16, 16, 0, Entity.SEED_SPRITE);
						Game.entities.add(seed);
						Game.seedCount++;
					} else if(pixelAtual == 0xFFFF0000) {
						// Frutas para o pac man comer e deixar os fantasmas parados
						Fruit fruit = new Fruit(xx * 16, yy * 16, 16, 16, 0, Entity.FRUIT_SPRITE);
						Game.entities.add(fruit);
						Game.fruitCount++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isFree(int xNext, int yNext) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;
		
		int x2 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;
		
		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + TILE_SIZE - 1) / TILE_SIZE;
		
		int x4 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (yNext + TILE_SIZE - 1) / TILE_SIZE;
		
		if(!(tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile ||
			 tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile ||
			 tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile ||
			 tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile)) {
			
			return true;
		}
		
		return false;
	}
	
	public static void restartGame() {
		Game.gameState = "GAMEOVER";
		Game.ghostMode = false;
		Game.entities.clear();
		Game.player = new Player(0, 0, 16, 16, 2, null);
		Game.entities.add(Game.player);
		Game.fruitCount = 0;
		Game.seedCount = 0;
		Game.world = new World("/level1.png");
		
		return;
	}
	
	public void Render(Graphics g) {
		int xStart = Camera.x >> 4; // >> 4 mesmo que dividir por 16
		int yStart = Camera.y >> 4;
		int xFinal = xStart + (Game.WIDTH >> 4);
		int yFinal = yStart + (Game.HEIGHT >> 4);
		
		for(int xx = xStart; xx <= xFinal; xx++) {
			for(int yy = yStart; yy <= yFinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) { // Corrigi problema com números negativos
					continue;
				}
				
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(g);
			}
		}
	}
}
