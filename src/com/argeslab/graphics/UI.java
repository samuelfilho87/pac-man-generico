package com.argeslab.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.argeslab.main.Game;

public class UI {
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 18));
		g.drawString("Sementes: " + Game.seedCount, 30, 25);
		g.drawString("Frutas: " + Game.fruitCount, 200, 25);
	}
}
