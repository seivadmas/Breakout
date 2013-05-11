/*
 * File: Breakout.java
 * -------------------
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board
 *  Should not be used directly (use getWidth()/getHeight() instead).
 *  * */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;
	
/** Offset of the leftmost brick column from the left */
	private static final int BRICK_X_OFFSET = BRICK_SEP / 2; // fudge factor, adjust to something that looks good

/** Number of turns */
	private static final int NTURNS = 3;
	
/** Pause time between updating animation frames */
	private static final int PAUSE_TIME = 20; // in ms
	
/** Initialise paddle object */
	private GRect PADDLE;
	
/** paddle y origin */
	private int PADDLE_Y_ORIGIN = APPLICATION_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT / 2;
	
/** Initialise ball object */
	private GOval BALL;
	private double vx, vy; //velocity components of ball
	
/** Ball initial position */
	private int BALL_X_INITIAL = APPLICATION_WIDTH / 2;
	private int BALL_Y_INITIAL = APPLICATION_HEIGHT / 2;
	
/** Instantiate random number generator */
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setUpGame();
		runGame();
	}
	
	/** sets up the game board with colored bricks */
	private void setUpGame() {
		// draw bricks
		for (int i = 0; i < NBRICK_ROWS; i++) {
			Color brickColor;
			if (i < 2) {
				brickColor = Color.red;
			} else if (i < 4) {
				brickColor = Color.orange;
			} else if (i < 6) {
				brickColor = Color.yellow;
			} else if (i < 8) {
				brickColor = Color.green;
			} else {
				brickColor = Color.cyan;
			}
			drawRow(BRICK_X_OFFSET, (BRICK_Y_OFFSET + i * (BRICK_SEP + BRICK_HEIGHT)), brickColor);
		}
		// set up mouse listening
		addMouseListeners();
		// initialise and add paddle
		initPaddle(APPLICATION_WIDTH / 2);
	}
	
	/** 
	 * Draws a row of bricks
	 * 
	 * @param x origin of row (left)
	 * @param y origin of row (top)
	 * @param color of row
	 */
	private void drawRow(int x, int y, Color color) {
		for (int i = 0; i < NBRICKS_PER_ROW; i++) {
			GRect brick = new GRect((x + i * (BRICK_WIDTH + BRICK_SEP)), y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setFillColor(color);
//			brick.setColor(color); // uncomment to remove borders
			add(brick);
		}
	}
	
	/** 
	 * Runs the main game program
	 */
	private void runGame() {
		// initialise ball object
		initBall(BALL_X_INITIAL, BALL_Y_INITIAL);
		// kick the ball off with some initial velocity
		vy = 2.0;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		/* Main animation loop */
		while (true) {
			BALL.move(vx, vy);
			pause(PAUSE_TIME);
			// bounce if we hit a wall
			if (ballHitVerticalWall()) {
				vx = -vx;
			}
			if (ballHitHorizontalWall()) {
				vy = -vy;
			}
			// bounce if we hit a brick
			for (int i = 0; i < 4; i++) {
				if (getElementAt(BALL.getX(), BALL.getY()) != null) {
					println("There's an element at: " + BALL.getX() + " , " + BALL.getY());
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private boolean ballHitVerticalWall() {
		if (BALL.getX() <= 0 || BALL.getX() >= (APPLICATION_WIDTH - (BALL_RADIUS * 2))) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 */
	private boolean ballHitHorizontalWall() {
		if (BALL.getY() <= 0 || BALL.getY() >= (APPLICATION_HEIGHT - (BALL_RADIUS * 2))) {
			return true;
		} else {
			return false;
		}
	}
	
	/** 
	 * Initialises ball object at:
	 * 
	 * @param x
	 * @param y
	 * 
	 * (coordinates describe center of ball)
	 * 
	 */
	private void initBall(int x, int y) {
		int x0 = x - BALL_RADIUS;
		int y0 = y - BALL_RADIUS;
		this.BALL = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		BALL.setLocation(x0, y0);
		BALL.setFilled(true);
		BALL.setFillColor(Color.black);
		add(this.BALL);
	}
	
	/**
	 * Listens for mouse movements and move paddle appropriately
	 * Paddle movement is restricted by bounds of application window
	 */
	public void mouseMoved(MouseEvent e) {
		int x;
		/* set bounds on paddle movement so it's edges cannot leave the screen */
		if (e.getX() < PADDLE_WIDTH / 2) {
			x = PADDLE_WIDTH / 2;
		} else if (e.getX() > (APPLICATION_WIDTH - PADDLE_WIDTH / 2)) {
			x = APPLICATION_WIDTH - PADDLE_WIDTH / 2;
		} else {
			x = e.getX();
		}
		this.PADDLE.setLocation((x - PADDLE_WIDTH / 2), PADDLE_Y_ORIGIN);
	}
		
	/** 
	 * Initialises paddle object at coordinates x, y
	 * 
	 * @param x coordinate of paddle 
	 * @param y coordinate of paddle
	 */
	private void initPaddle(int x) {
		int x0 = x - PADDLE_WIDTH / 2;
		this.PADDLE = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		PADDLE.setLocation(x0, PADDLE_Y_ORIGIN);
		PADDLE.setFilled(true);
		PADDLE.setFillColor(Color.black);
		add(this.PADDLE);
	}
}
