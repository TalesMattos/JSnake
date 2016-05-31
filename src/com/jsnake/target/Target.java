package com.jsnake.target;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.jsnake.ClockSeconds;
import com.jsnake.ClockSecondsListener;
import com.jsnake.JSnake;
import com.jsnake.SnakeGame;

public abstract class Target implements TargetHitAction {

	protected SnakeGame listener;
	private int xCoordinate;
	private int yCoordinate;
	private boolean show = true;
	private boolean doingStuff;
	private boolean hit = false;

	private ClockSeconds clockSecondsKeepAlive = new ClockSeconds(timeAlive()/1000, false, new ClockSecondsListener() {
		@Override
		public void timeout() {
			show = false;
		}
	});
	private ClockSeconds clockSecondsHitDoingStuff = new ClockSeconds(timeDoStuffOnHit()/1000, false, new ClockSecondsListener() {
		@Override
		public void timeout() {
			hit = false;
			undoStuff();
		}
	});
	
	
	public Target() {
		caculateNewCoordinate();
		if (timeAlive() > 0 && show) {
			clockSecondsKeepAlive.start();
		}
	}
	
	public abstract int recalculateScore(int currentScore);
	public abstract int resizeSnake(int currentSnakeSize);
	public abstract int calcDelayOnHit(); // in milliseconds
	public abstract int timeAlive(); // in milliseconds
	protected abstract int timeDoStuffOnHit();
	public abstract TargetType getTargetType();
	
	protected final boolean isMaxSpeed(int currentDelay) {
		return currentDelay <= JSnake.MAX_SPPED;
	}

	protected final boolean isMinSpeed(int currentDelay) {
		return currentDelay >= JSnake.MIN_SPPED;
	}
	
	public void stopTimerHitDoingStuff() {
		hit = false;
		doingStuff = false;
		clockSecondsHitDoingStuff.stop();
	}
	
	public ClockSeconds getClockSecondsKeepAlive() {
		return clockSecondsKeepAlive;
	}
	
	public ClockSeconds getClockSecondsHitDoingStuff() {
		return clockSecondsHitDoingStuff;
	}
	
	public boolean isShow() {
		return show;
	}
	
	public void setShow(boolean show) {
		this.show = show;
	}
	
	public boolean isCoordinateConflicted(int x, int y) {
		if (xCoordinate == x && yCoordinate == y) {
			return true;
		}
		return false;
	}
	
	public final void caculateNewCoordinate() {
		int random = (int) (Math.random() * (JSnake.PANEL_WIDTH / JSnake.COORDINATE_SIZE));
		int x = (random * JSnake.COORDINATE_SIZE);

		random = (int) (Math.random() * (JSnake.PANEL_HEIGHT / JSnake.COORDINATE_SIZE));
		int y = (random * JSnake.COORDINATE_SIZE);

		xCoordinate = x;
		yCoordinate = y;
	}
	
	/**
	 * currentDelay = delay atual
	 * millisec - Se +(positivo) diminui a velocidade
	 * 						Se -(negativo) aumenta a velocidade
	 */
	public final int recalculateSpeed(int currentDelay) {
		int millisec = calcDelayOnHit();
		if (millisec == 0)
			return currentDelay;
		if (millisec < 0) {
			if (!isMaxSpeed(currentDelay)) {
				if ((currentDelay + millisec) <= JSnake.MAX_SPPED)
					currentDelay = JSnake.MAX_SPPED;
				else
					currentDelay = currentDelay + millisec;
			}
		} else if (millisec > 0) {
			if (!isMinSpeed(currentDelay)) {
				if ((currentDelay + millisec) >= JSnake.MIN_SPPED)
					currentDelay = JSnake.MIN_SPPED;
				else
					currentDelay = currentDelay + millisec;
			}
		}
		return currentDelay;
	}
	
	public int getXCoordinate() {
		return xCoordinate;
	}
	
	public int getYCoordinate() {
		return yCoordinate;
	}
	
	public enum TargetType {
		STAR("star.png"),
		SLOW("slow.png"),
		DECREASE("decrease.jpg"), 
		APPLE("apple.jpg"), 
		FLY("fly.png");
		
		private ImageIcon imgIcon;
		
		private TargetType(String imgIcon) {
			this.imgIcon = new ImageIcon(SnakeGame.class.getResource("/" + imgIcon));
		}

		public Image getImage() {
			return this.imgIcon.getImage();
		}
	}
	
	@Override
	public void addSnakeGameListener(SnakeGame listener) {
		this.listener = listener;
	}

	@Override
	public void doStuff() {
		doingStuff = true;
		//sobrescrever e implementar. chamar super
	}
	
	@Override
	public void undoStuff() {
		doingStuff = false;
		//sobrescrever e implementar. chamar super
	}
	
	public boolean isDoingStuff() {
		return doingStuff;
	}
	
	public void hit() {
		hit = true;
		show = false;
		clockSecondsKeepAlive.stop();
		if (timeDoStuffOnHit() > 0){
			clockSecondsHitDoingStuff.start();
			doStuff();	
		}
	}
	
	public boolean isHit() {
		return hit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getTargetType() == null) ? 0 : getTargetType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Target other = (Target) obj;
		if (getTargetType() == null) {
			if (other.getTargetType() != null)
				return false;
		} else if (!getTargetType().equals(other.getTargetType()))
			return false;
		return true;
	}
	
	
	
}