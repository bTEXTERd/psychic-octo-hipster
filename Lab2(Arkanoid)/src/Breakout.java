import acm.program.*; 
import acm.util.MediaTools;
import acm.util.RandomGenerator;
import acm.graphics.*; 

import java.applet.AudioClip;
import java.awt.*; 
import java.awt.event.*; 


public class Breakout extends GraphicsProgram { 
	
	
/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 600;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	public static final int WIDTH = APPLICATION_WIDTH;
	public static final int HEIGHT = APPLICATION_HEIGHT;

/** Animation cycle delay */ 
	public static final int DELAY = 5;

/** Dimensions of the paddle */
	public static final int BAT_WIDTH = 75;
	public static final int BAT_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	public static final int BAT_Y_OFFSET = 30;

/** Number of bricks per row */
	public static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	public static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	public static final int BRICK_SEP = 4;

/** Width of a brick */
	public static final int BRICK_WIDTH = (APPLICATION_WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	public static final int BRICK_HEIGHT = 10;

/** Radius of the ball in pixels */
	public static final int BALL_DIAM = 20;

/** Offset of the top brick row from the top */
	public static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
		
	
	public void run(){
		this.setSize(APPLICATION_WIDTH + 200,APPLICATION_HEIGHT);
		setup(); 
		playGame();	
	} 

	public void mouseMoved(MouseEvent e){
		
		if(e.getX() + bat.getWidth()/2 <= WIDTH){
			if(e.getX() >= bat.getWidth()/2){
				bat.setLocation(e.getX() - bat.getWidth() / 2,HEIGHT - bat.getHeight() - BAT_Y_OFFSET);
				if(clicked){
					ball.setLocation(e.getX() - ball.getWidth() / 2, HEIGHT - bat.getHeight() - ball.getHeight() - BAT_Y_OFFSET);
					}
			}
		}	
	}
	
	public void setup() {
		drawBat();
		drawBricks((getWidth())*1.5, BRICK_Y_OFFSET);
		drawBall();
		drawStuff();
		addMouseListeners();
	}
	
	public void drawStuff() {

		GLine line = new GLine(WIDTH + 2,0,WIDTH + 2,HEIGHT);
		add(line);
		
		life = new GLabel("YOU HAVE " + lives + " LIVES");
		life.setFont("Times New Roman-15");
		add(life, WIDTH + 35,100);
		
		score1 = new GLabel("YOUR'S SCORE: " + score);
		add(score1,WIDTH + 50,300);
		
	}
			

	public void drawBall() {
		ball = new GOval(bat.getX() + (bat.getWidth()/2-BALL_DIAM/2), bat.getY() - BALL_DIAM, BALL_DIAM, BALL_DIAM);
		ball.setFilled(true);
		ball.setColor(rgen.nextColor());
		add(ball);
	}
	
	public void playGame() {
		getBallVelocity();
		waitForClick();
		clicked = false;
		while (true) {
			if(brickCounter != 0){
				moveBall();
				pause(DELAY);
			}
			else if(brickCounter == 0){
				if(stop) removeAll();
				stop=false;
				GLabel winner = new GLabel("YOU WIN!!");
				winner.setFont("Times New Roman-46");
				double x = (getWidth()-winner.getWidth())/2;
				double y = (getHeight()- winner.getAscent())/2;
				add(winner,x,y);
			}
		}
	}

	public void getBallVelocity() {
		vy = -1.0;
		vx = rgen.nextDouble(0.8, 1.6);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx; 
		}
	}
	

	public void moveBall() {
		ball.move(vx, vy);
		
		if ((ball.getX() - vx <= 0 && vx < 0 )|| (ball.getX() + vx >= (WIDTH - BALL_DIAM ) && vx > 0)) {
			vx = -vx;
			ball.setColor(rgen.nextColor());
		}
		if (ball.getY() - vy <= 0 && vy < 0 ){
			vy = -vy;
			ball.setColor(rgen.nextColor());
		}
		
		GObject collider = getCollidingObject();
		
		if(collider == bat) {
 
			if(ball.getY() + BALL_DIAM >= bat.getY()){ 
                vy = -vy; 
                ball.setColor(rgen.nextColor());
            }
        }
		else if(collider != null){
			bonus();
			bounceClip.play();
			brickCounter--;
			score += 50;
			score1.setLabel("YOUR'S SCORE: " + score);
			remove(collider);
			vy = -vy;
			ball.setColor(rgen.nextColor());
			
		}
		
		if (ball.getY() + vy >= (getHeight() + BALL_DIAM ) && vy>0){
			if(turn < NTURNS){
				lives--;
				if(lives == 1) life.setLabel("YOU HAVE " + lives + " LIFE");
				else life.setLabel("YOU HAVE " + lives + " LIVES");
				turn++;
				remove(ball);
				clicked=true;
				drawBall();
				playGame();
			}
			else{
				removeAll();
				GLabel loser = new GLabel("YOU LOSE!!");
				loser.setFont("Times New Roman-46");
				double x = (getWidth()-loser.getWidth())/2;
				double y = (getHeight()- loser.getAscent())/2;
				add(loser,x,y);
			}
		}
	}
	
	public void bonus() {
		GObject collider = getCollidingObject();
		GRect rect = new GRect (0,0,30,30);
		rect.setFilled(true);
		add(rect,(collider.getWidth() - rect.getWidth())/2, collider.getY() + BRICK_HEIGHT);
		
	}

	public void drawBricks(double cx, double cy) {             
		 
        // потрібно два цикли, щоб створити ряди і колони з цеглин
 
        for( int row = 0; row < NBRICK_ROWS; row++ ) {
 
            for (int column = 0; column < NBRICKS_PER_ROW; column++) {
 
                double  x = cx - (NBRICKS_PER_ROW*BRICK_WIDTH)/2 - ((NBRICKS_PER_ROW-1)*BRICK_SEP)/2 + column*BRICK_WIDTH + column*BRICK_SEP;
 
                double  y = cy + row*BRICK_HEIGHT + row*BRICK_SEP;
 
                brick = new GRect( x , y , BRICK_WIDTH , BRICK_HEIGHT );
                brick.setFilled(true);
                add(brick);
                brick.setFilled(true);
 
                //задаємо колір цеглинам залежно від ряду
 
                if (row < 2) {
                    brick.setColor(Color.RED);
                }
                if (row == 2 || row == 3) {
                    brick.setColor(Color.ORANGE);
                }
                if (row == 4 || row == 5) {
                    brick.setColor(Color.YELLOW);
                }
                if (row == 6 || row == 7) {
                    brick.setColor(Color.GREEN);
                }
                if (row == 8 || row == 9) {
                    brick.setColor(Color.CYAN);
                }
            }
        }
    }
	
	
	public GObject getCollidingObject() {
		 
        if((getElementAt(ball.getX(), ball.getY())) != null) {
             return getElementAt(ball.getX(), ball.getY());
          }
        else if (getElementAt( (ball.getX() + BALL_DIAM), ball.getY()) != null ){
             return getElementAt(ball.getX() + BALL_DIAM, ball.getY());
          }
        else if(getElementAt(ball.getX(), (ball.getY() + BALL_DIAM)) != null ){
             return getElementAt(ball.getX(), ball.getY() + BALL_DIAM);
          }
        else if(getElementAt((ball.getX() + BALL_DIAM), (ball.getY() + BALL_DIAM)) != null ){
             return getElementAt(ball.getX() + BALL_DIAM, ball.getY() + BALL_DIAM);
          }
        //повертає null якщо ні один об'єкт не присутній
        else{
             return null;
          }
    }	
	
	
	public void drawBat(){
	
		bat = new GRect((getWidth() - BAT_WIDTH)/2, APPLICATION_HEIGHT - BAT_HEIGHT - BAT_Y_OFFSET, BAT_WIDTH, BAT_HEIGHT); 
		bat.setFilled(true);
		add(bat);
	}

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	public int score = 0;
	public int lives = 3;
	public GLabel life;
	public GLabel score1;
	public int turn = 1;
	public int brickCounter = 100;
	public double vx, vy;
	public GRect brick;
	public GRect bat;
	public GOval ball;	
	public boolean clicked = true;
	public boolean stop = true;
	public RandomGenerator rgen = RandomGenerator.getInstance();
}
