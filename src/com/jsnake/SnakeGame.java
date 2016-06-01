package com.jsnake;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.jsnake.SnakeKeypadListener.KeypadSnake;
import com.jsnake.target.AppleTarget;
import com.jsnake.target.DecreaseTarget;
import com.jsnake.target.FlyTarget;
import com.jsnake.target.SlowTarget;
import com.jsnake.target.StarTarget;
import com.jsnake.target.Target;
import com.jsnake.target.TargetHitAction;


public class SnakeGame extends JPanel implements TargetHitAction {

	private static final long serialVersionUID = 1L;

	private int[] evilSnakeCoordinateX = new int[JSnake.ALL_COORDINATES];
	private int[] evilSnakeCoordinateY = new int[JSnake.ALL_COORDINATES];
	private Image evilSnakeImage = (new ImageIcon(SnakeGame.class.getResource("/evil_snake.png"))).getImage();
	private boolean showEvilSnake = false;
	private KeypadSnake keypadEvilSnake = KeypadSnake.NULL;
	private int evilSnakeSize = 4;
	private Timer timerEvilSnakeChangeDirection = new Timer(1000, new ListenerEvilSnakeChangeDirection());
	private Timer timerEvilSnakeMove = new Timer(100, new ListenerEvilSnakeMove());

	private int[] snakeCoordinateX; 
	private int[] snakeCoordinateY;
	private Image snakeBodyImage;
	private Image snakeHeadImage;
	private Timer timerActionPerformed;
	private boolean playing;
	private ControlPanel controlPanel = new ControlPanel();
	private final SnakeKeypadListener keypad = new SnakeKeypadListener();
	
	private AbstractQueue<Target> targets = new ConcurrentLinkedQueue<Target>();
	private Target targetDoingStuff;
	private Target targetAux;

	public SnakeGame() {
		addKeyListener(keypad);
		setBackground(Color.WHITE);
		setFocusable(true);
		setSize(JSnake.PANEL_WIDTH, JSnake.PANEL_HEIGHT);
		startGame();
	}

	@Override
	public void changeSuperSnake(boolean superSnake) {
		controlPanel.setSuperSnake(superSnake);
		ImageIcon snakeBody = new ImageIcon(SnakeGame.class.getResource(superSnake ? "/body_super.png" : "/body.jpg"));
		ImageIcon snakeHead = new ImageIcon(SnakeGame.class.getResource(superSnake ? "/head_super.png" : "/head.jpg"));
		snakeBodyImage = snakeBody.getImage();
		snakeHeadImage = snakeHead.getImage();
	}
	
	private void configure() {
		targets.clear();
		targets.add(new FlyTarget());
		keypad.configure();
		snakeCoordinateX = new int[JSnake.ALL_COORDINATES];
		snakeCoordinateY = new int[JSnake.ALL_COORDINATES];
		changeSuperSnake(false);
		controlPanel.configure();
		playing = true;
		for (int i = 0; i < controlPanel.getSnakeSize(); i++) {
			snakeCoordinateX[i] = (JSnake.COORDINATE_SIZE * (controlPanel.getSnakeSize() + 2)) - (i * JSnake.COORDINATE_SIZE);
			snakeCoordinateY[i] = (JSnake.COORDINATE_SIZE * (controlPanel.getSnakeSize() + 2));
		}
		stopEvilSnake();
		if (timerActionPerformed == null) {
			timerActionPerformed = new Timer(controlPanel.getSnakeDelay(), new ListenerGameVerifier());
		} else
			timerActionPerformed.setDelay(controlPanel.getSnakeDelay());
		timerActionPerformed.start();
	}
	
	private void startGame() {
		configure();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (controlPanel.isDrawGameOver()) {
			try {
				Thread.sleep(4000);
				this.startGame();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (playing) {
			if (!targets.isEmpty()) {
				for (Target tAux : targets) {
					if (tAux.isShow())
						g.drawImage(tAux.getTargetType().getImage(), tAux.getXCoordinate(), tAux.getYCoordinate(), this);				
				}
			}
			for (int i = 0; i < controlPanel.getSnakeSize(); i++) {
				if (i == 0) {
					g.drawImage(snakeHeadImage, snakeCoordinateX[i], snakeCoordinateY[i], this);
				} else {
					g.drawImage(snakeBodyImage, snakeCoordinateX[i], snakeCoordinateY[i], this);
				}
			}
			if (showEvilSnake) {
				for (int i = 0; i < evilSnakeSize; i++) 
					g.drawImage(evilSnakeImage, evilSnakeCoordinateX[i], evilSnakeCoordinateY[i], this);
			}
			controlPanel.drawInfo(g);
			if (targetAux != null && targetAux.timeAlive() > 0 && targetAux.isShow()) {
				Font infoFont = new Font("Courrier", Font.BOLD, 12);
				String info = targetAux.getTargetType().name() + " " + targetAux.getClockSecondsKeepAlive().toString();
				FontMetrics infoMetric = g.getFontMetrics(infoFont);
				controlPanel.drawInfo(g, info, (JSnake.PANEL_WIDTH - infoMetric.stringWidth(info)) - JSnake.COORDINATE_SIZE, JSnake.COORDINATE_SIZE * 2);				
			}
			if (targetDoingStuff != null && targetDoingStuff.isDoingStuff()) {
				String label;
				if (targetDoingStuff instanceof StarTarget)
					label = "Super ";
				else
					label = targetDoingStuff.getTargetType().name() + " ";
				controlPanel.drawInfo(g, label + targetDoingStuff.getClockSecondsHitDoingStuff().toString(), JSnake.COORDINATE_SIZE, JSnake.COORDINATE_SIZE * 2);
			}
				
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		} else {
			targetAux = null;
			targetDoingStuff = null;
			controlPanel.gameOver(g);
		}
	}

	private void checkHit(Target target) {
		if (isSnakeHitTarget(target)) {
			if (targetDoingStuff != null && target.equals(targetDoingStuff)) {
				targetDoingStuff.stopTimerHitDoingStuff();
			}
			for (Target targetItem : targets) {
				if (!targetItem.isShow())
					targets.remove(targetItem);
			}
			target.hit();
			if (target.isDoingStuff())
				targetDoingStuff = target;
			recalculateOnHitTarget(target);
			target.setShow(false);
			targets.remove(target);
			createNewTarget(target);
			startEvilSnake();
		}
	}
	
	private boolean isSnakeHitTarget(Target target) {
		return (snakeCoordinateX[0] == target.getXCoordinate()) && (snakeCoordinateY[0] == target.getYCoordinate());
	}
	
	private void recalculateOnHitTarget(Target target) {
		controlPanel.incrementCountHitTarget();
		controlPanel.setScore(target.recalculateScore(controlPanel.getScore()));
		controlPanel.setSnakeSize(target.resizeSnake(this.controlPanel.getSnakeSize()));
		int delay;
		timerActionPerformed.setDelay(delay = target.recalculateDelay(this.timerActionPerformed.getDelay()));		
		controlPanel.setSnakeDelay(delay);
	}
	
	private void moveSnake() {
		for (int i = controlPanel.getSnakeSize(); i > 0; i--) {
			snakeCoordinateX[i] = snakeCoordinateX[(i - 1)];
			snakeCoordinateY[i] = snakeCoordinateY[(i - 1)];
		}
		
		switch (keypad.getKeyPressed()) {
		case LEFT:
			// Se foi para esquerda decrementa em x
			snakeCoordinateX[0] -= JSnake.COORDINATE_SIZE;
			break;
		case RIGHT:
			// Se foi para direita incrementa em x
			snakeCoordinateX[0] += JSnake.COORDINATE_SIZE;
			break;
		case UP:
			// Se foi para cima decrementa em y
			snakeCoordinateY[0] -= JSnake.COORDINATE_SIZE;
			break;
		case DOWN:
			// Se foi para baixo incrementa em y
			snakeCoordinateY[0] += JSnake.COORDINATE_SIZE;			
			break;
		default:
			break;
		}
		
		if (!KeypadSnake.NULL.equals(keypad.getKeyPressed())) {
			controlPanel.startClock();
		}
	}
	
	private void moveEvilSnake() {
		if (isEvilSnakeHitBorder()) {
			if (evilSnakeCoordinateY[0] > JSnake.PANEL_HEIGHT) 
				keypadEvilSnake = KeypadSnake.UP;
			if (evilSnakeCoordinateY[0] < 0) 
				keypadEvilSnake = KeypadSnake.DOWN;
			if (evilSnakeCoordinateX[0] > JSnake.PANEL_WIDTH) 
				keypadEvilSnake = KeypadSnake.LEFT;
			if (evilSnakeCoordinateX[0] < 0) 
				keypadEvilSnake = KeypadSnake.RIGHT;
		} 
		for (int i = evilSnakeSize; i > 0; i--) {
			evilSnakeCoordinateX[i] = evilSnakeCoordinateX[(i - 1)];
			evilSnakeCoordinateY[i] = evilSnakeCoordinateY[(i - 1)];
		}
		switch (keypadEvilSnake) {
		case LEFT:
			// Se foi para esquerda decrementa em x
			evilSnakeCoordinateX[0] -= JSnake.COORDINATE_SIZE;
			break;
		case RIGHT:
			// Se foi para direita incrementa em x
			evilSnakeCoordinateX[0] += JSnake.COORDINATE_SIZE;
			break;
		case UP:
			// Se foi para cima decrementa em y
			evilSnakeCoordinateY[0] -= JSnake.COORDINATE_SIZE;
			break;
		case DOWN:
			// Se foi para baixo incrementa em y
			evilSnakeCoordinateY[0] += JSnake.COORDINATE_SIZE;			
			break;
		default:
			break;
		}
		checkHitEvilSnake();			
	}
	
	private void changeDirectionEvilSnake() {
		int random = (int) (Math.random() * 4);
		KeypadSnake novaDirecao = KeypadSnake.values()[random];
		if (keypadEvilSnake.equals(novaDirecao))
			return;
		else
			keypadEvilSnake = novaDirecao;
	}

	private boolean isEvilSnakeHitBorder() {
		if (evilSnakeCoordinateY[0] > JSnake.PANEL_HEIGHT - 1) {
			return true;
		}
		if (evilSnakeCoordinateY[0] < 0 + 1) {
			return true;
		}
		if (evilSnakeCoordinateX[0] > JSnake.PANEL_WIDTH - 1) {
			return true;
		}
		if (evilSnakeCoordinateX[0] < 0 + 1) {
			return true;
		}
		return false;
	}
	
	private void checkHitBorder() {
		for (int i = controlPanel.getSnakeSize(); i > 0; i--) {
			if ((i > 4) && (snakeCoordinateX[0] == snakeCoordinateX[i]) && (snakeCoordinateY[0] == snakeCoordinateY[i])) {
				playing = false;
			}
		}
		if (snakeCoordinateY[0] > JSnake.PANEL_HEIGHT) {
			playing = false;
		}
		if (snakeCoordinateY[0] < 0) {
			playing = false;
		}
		if (snakeCoordinateX[0] > JSnake.PANEL_WIDTH) {
			playing = false;
		}
		if (snakeCoordinateX[0] < 0) {
			playing = false;
		}
		
	}
	
	private void checkHitEvilSnake() {
		for (int i = 0; i < evilSnakeSize; i++) {
			if (snakeCoordinateX[0] == evilSnakeCoordinateX[i] && snakeCoordinateY[0] == evilSnakeCoordinateY[i]) {
				if (controlPanel.isSuperSnake())
					stopEvilSnake();
				else
					playing = false;
			}
		}
		for (int i = 0; i < controlPanel.getSnakeSize(); i++) {
			if (evilSnakeCoordinateX[0] == snakeCoordinateX[i] && evilSnakeCoordinateY[0] == snakeCoordinateY[i]) {
				if (controlPanel.isSuperSnake())
					stopEvilSnake();
				else
					playing = false;
			}
		}
	}
	
	private void createNewTarget(Target target) {
		Target newTargetDefault = null;
		if (target instanceof FlyTarget) {
			newTargetDefault = new FlyTarget();						
		} 
		addNewTarget(newTargetDefault);
		if (targets.size() >= 2)
			return;
		Target newTargetAux = null;
		if (controlPanel.getCountHitTarget() >= 10) {
			if (controlPanel.getCountHitTarget() % 10 == 0) {
				newTargetAux = new AppleTarget();
			} else if (controlPanel.getCountHitTarget() % 10 == 1) {
				newTargetAux = new SlowTarget();
			} else if (controlPanel.getCountHitTarget() % 10 == 2) {
				newTargetAux = new DecreaseTarget();			
			} else if (controlPanel.getScore() >= 20 && controlPanel.getCountHitTarget() % 10 == 3) {
				newTargetAux = new StarTarget(this);			
			}
		}
		targetAux = newTargetAux;
		if (newTargetAux != null)
			addNewTarget(newTargetAux);
	}
	
	private void addNewTarget(Target newTarget) {
		if (newTarget != null) {
			while (isCoordinateConflicted(newTarget)) {
				newTarget.caculateNewCoordinate();
			} 
			targets.add(newTarget);
		}		
	}

	private boolean isCoordinateConflicted(Target newTarget) {
		int snakeX = this.snakeCoordinateX[newTarget.getXCoordinate()];
		int snakeY = this.snakeCoordinateY[newTarget.getYCoordinate()];
		for (Target targetItem : targets) {
			if (newTarget.isCoordinateConflicted(targetItem.getXCoordinate(), targetItem.getYCoordinate())
					|| newTarget.isCoordinateConflicted(snakeX, snakeY)) 
				return true;
		}
		return false;
	}
	
	private class ListenerGameVerifier implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (playing) {
				if (!targets.isEmpty()) {
					for (Target tAux : targets)
						checkHit(tAux);
				}
				if (showEvilSnake) {
					checkHitEvilSnake();					
				}
				checkHitBorder();
				moveSnake();
			}
			repaint();
		}
	}

	private class ListenerEvilSnakeChangeDirection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (playing && showEvilSnake) {
				changeDirectionEvilSnake();
			}
		}
	}

	private class ListenerEvilSnakeMove implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (playing && showEvilSnake) {
				moveEvilSnake();
				repaint();
			}
		}
	}
	
	
	private void startEvilSnake() {
		if (showEvilSnake)
			return;
		if (controlPanel.getScore() >= 20 && controlPanel.getCountHitTarget() % 10 == 0) {
			for (int i = 0; i < evilSnakeSize; i++) {
				evilSnakeCoordinateX[i] = 30 - (i * JSnake.COORDINATE_SIZE);
				evilSnakeCoordinateY[i] = 0;
			}
			showEvilSnake = true;
			timerEvilSnakeChangeDirection.start();
			timerEvilSnakeMove.start();			
		}
	}
	
	private void stopEvilSnake() {
		if (!showEvilSnake)
			return;
		evilSnakeCoordinateX = new int[JSnake.ALL_COORDINATES]; 
		evilSnakeCoordinateY = new int[JSnake.ALL_COORDINATES];
		showEvilSnake = false;
		timerEvilSnakeChangeDirection.stop();
		timerEvilSnakeMove.stop();
	}

}
