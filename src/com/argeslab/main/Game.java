package com.argeslab.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import com.argeslab.entities.Entity;
import com.argeslab.entities.Player;
import com.argeslab.graphics.Spritesheet;
import com.argeslab.graphics.UI;
import com.argeslab.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 240;
	public static final int SCALE = 2;
	private BufferedImage image;
	public static List<Entity> entities;
	public static Spritesheet spritesheet;
	public static World world;
	public static Player player;
	public UI ui;
	public static int fruitCount = 0;
	public static int seedCount = 0;
	public static boolean ghostMode = false;
	public static String gameState = "INICIO";
	public static boolean venceu = false;
	
	public Game() {
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		//this.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize())); // full screen
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE)); 	// janela tamanho fixo
		
		initFrame();
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		// Inicializando objetos
		spritesheet = new Spritesheet("/spritesheet.png"); 
		player = new Player(0, 0, 16, 16, 2, null);
		entities = new ArrayList<Entity>();
		world = new World("/level1.png");
		ui = new UI();
		Sound.musicEffect.play();
		
		entities.add(player);
	}
	
	public void initFrame() {
		frame = new JFrame("Pac-man Generico");
		frame.add(this);
		//frame.setUndecorated(true); // faz parte do fullscreen, remove barra de título da janela JAVA
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		// Icone da janela
		Image icon = null;
		
		try {
			icon = ImageIO.read(getClass().getResource("/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.setIconImage(icon);
	}
	
	public synchronized void start() {		
		thread = new Thread(this);
		
		isRunning = true;
		thread.start();
		
	}
	
	public synchronized void stop() {
		isRunning = false;
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		
		game.start();
	}
	
	public void tick() {
		if(gameState == "JOGANDO") {
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.getGraphics();
		
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/* Renderização do jogo */
		//Graphics2D g2 = (Graphics2D) g;
		world.Render(g);
		
		Collections.sort(entities, Entity.depthSorter);
		
		for(int i = 0; i < entities.size(); i++) {
			entities.get(i).render(g);
		}
		
		/**/
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		ui.render(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		if(gameState == "INICIO" || gameState == "PAUSE" || gameState == "GAMEOVER") {
			g2.setColor(new Color(0, 0, 0, 200));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			
			g.setFont(new Font("arial", Font.BOLD, 26));
			g.setColor(Color.white);
			
			if(gameState == "INICIO") {						
				g.drawString("Pressione Enter para começar.", (WIDTH * SCALE) / 2 - 190, (HEIGHT * SCALE) / 2);
			}
			
			if(gameState == "PAUSE") {			
				g.setFont(new Font("arial", Font.BOLD, 36));
				g.drawString("Pause", (WIDTH * SCALE) / 2 - 55, (HEIGHT * SCALE) / 2);
			}
			
			if(gameState == "GAMEOVER") {
				if(venceu) {
					g.setFont(new Font("arial", Font.BOLD, 30));
					g.setColor(Color.green);
					g.drawString("PARABÉNS VOCÊ GANHOU!", (WIDTH * SCALE) / 2 - 205, (HEIGHT * SCALE) / 2 - 20);
					
					g.setFont(new Font("arial", Font.BOLD, 20));
					g.setColor(Color.white);
					g.drawString("Pressione Enter para jogar.", (WIDTH * SCALE) / 2 - 130, (HEIGHT * SCALE) / 2 + 20);
				} else {
					venceu = false;
					g.setFont(new Font("arial", Font.BOLD, 24));
					g.setColor(Color.red);
					g.drawString("VOCÊ FOI LENTO DEMAIS E PERDEU!", (WIDTH * SCALE) / 2 - 220, (HEIGHT * SCALE) / 2 - 20);
					
					g.setFont(new Font("arial", Font.BOLD, 20));
					g.setColor(Color.white);
					g.drawString("Pressione Enter para tentar novamente.", (WIDTH * SCALE) / 2 - 186, (HEIGHT * SCALE) / 2 + 20);
				}
			}
		}
		
		bs.show();
	}

	@Override
	public void run() {
		//Sound.music.loop();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		
		while(isRunning) {
			long now = System.nanoTime();
			
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				//System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {			
			if(gameState == "INICIO" || gameState == "PAUSE" || gameState == "GAMEOVER") {
				gameState = "JOGANDO";
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {			
			if(gameState == "JOGANDO") {
				gameState = "PAUSE";
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

}
