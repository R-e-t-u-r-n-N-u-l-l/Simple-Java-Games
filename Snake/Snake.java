package games;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Snake extends JPanel implements KeyListener{
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private Random r = new Random();
	private int foodX, foodY, keys, length = 3, speed = 200, size = 16;
	private int[] snakeX, snakeY;
	private boolean running = true, move = true;
	private boolean up, down, left, right;
	private long time;
	private final int width = 800,
					  height = 640;
	
	public Snake(String title) {
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		this.setSize(width, height);
		frame.addKeyListener(this);
		frame.add(this);
		
		time = System.currentTimeMillis();
		start();
	}
	
	public void start() {
		//Initialize snake & food
		foodX = r.nextInt(width / size);
		foodY = r.nextInt(height / size);
		snakeX = new int[(width / size) * (height / size)];
		snakeY = new int[(width / size) * (height / size)];
		
		for(int i = 0; i < length; i++) {
			snakeX[i] = 64;
			snakeY[i] = 64 - size * i;
		}
		
		//Game loop
		long lastTime = System.nanoTime();
		double tickAmt = 60;
		double ns = 1000000000 / tickAmt;
		double delta = 0;
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				update();
				this.repaint();
				delta--;
			}
		}
	}
	
	public void update() {
		//Snake movement
		if(System.currentTimeMillis() - time > speed) {
			time = System.currentTimeMillis();
			
			if(left || right || down || up) {
				for(int i = length; i > 0; i--) {
					snakeX[i] = snakeX[i - 1];
					snakeY[i] = snakeY[i - 1];
				}
			}
			
			if(right)
				snakeX[0] += size;
			else if(left)
				snakeX[0] -= size;
			else if(down)
				snakeY[0] += size;
			else if(up)
				snakeY[0] -= size;
		}
		
		//Food collision
		if(foodX * size - snakeX[0] < size && foodX * size - snakeX[0] > -size && foodY * size - snakeY[0] < size && foodY * size - snakeY[0] > -size) {
			foodX = r.nextInt(width / size);
			foodY = r.nextInt(height / size);
			length++;
		}
		
		//Body collision
		for(int i = 2; i < length; i++) {
			if(snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
				length = 3;
				for(int j = 0; j < length; j++) {
					snakeX[j] = 64;
					snakeY[j] = 64 - size * j;
				}
			}
		}
		
		//Wall collision
		if(snakeX[0] < 0)
			snakeX[0] = width - size;
		if(snakeX[0] + size > width)
			snakeX[0] = 0;
		if(snakeY[0] < 0)
			snakeY[0] = height - size;
		if(snakeY[0] + size > height)
			snakeY[0] = 0;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.clearRect(0, 0, width, height);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setColor(new Color(20, 20, 20));
		
		for(int i = 0; i < width / size; i++)
			g.drawLine(size + i * size,  0,  size + i * size,  height);
		
		for(int i = 0; i < height / size; i++)
			g.drawLine(0, size + i * size, width, size + i * size);
		
		draw(g);
		
		g.setColor(Color.WHITE);
		g.drawString(("Score: " + (length - 3)), 5, 12);
	}
	
	public void draw(Graphics g) {
		for(int i = 0; i < length; i++) {
			g.setColor(Color.GREEN);
			g.fillRect(snakeX[i], snakeY[i], size, size);
			g.setColor(new Color(20, 120, 20));
			g.drawRect(snakeX[i], snakeY[i], size, size);
		}
		
		g.setColor(Color.RED);
		g.fillOval(foodX * size, foodY * size, size, size);
		
		g.setColor(new Color(20, 180, 20));
		g.fillRect(snakeX[0], snakeY[0], size, size);
		g.setColor(new Color(20, 120, 20));
		g.drawRect(snakeX[0], snakeY[0], size, size);
	}
	
	public static void main(String[] args) {
		new Snake("Snake");
	}
	
	public void checkDir(int dir) {
		if(right && dir != 0)
			right = false;
		if(left && dir != 1)
			left = false;
		if(down && dir != 2)
			down = false;
		if(up && dir != 3)
			up = false;
	}

	public void keyReleased(KeyEvent e) {
		keys = e.getKeyCode();
		
		if(move) {
			if(keys == KeyEvent.VK_D && !left) {
				right = true;
				checkDir(0);
			} else if(keys == KeyEvent.VK_A && !right) {
				left = true;
				checkDir(1);
			} else if(keys == KeyEvent.VK_S && !up) {
				down = true;
				checkDir(2);
			} else if(keys == KeyEvent.VK_W && !down) {
				up = true;
				checkDir(3);
			}
		}
		
		//speed up/down
		if(keys == KeyEvent.VK_UP)
			speed -= 10;
		if(keys == KeyEvent.VK_DOWN)
			speed += 10;
	}
	
	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}