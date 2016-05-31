package com.jsnake.target;

import com.jsnake.SnakeGame;


public class StarTarget extends Target {

	public StarTarget(SnakeGame listener) {
		super.addSnakeGameListener(listener);
	}
	
	@Override
	public int recalculateScore(int currentScore) {
		return (currentScore + 7);
	}

	@Override
	public int resizeSnake(int currentSnakeSize) {
		if (currentSnakeSize > 3)
			return --currentSnakeSize;
		return currentSnakeSize;
	}

	@Override
	public int calcDelayOnHit() {
		return +10;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.STAR;
	}
	
	@Override
	public int timeAlive() {
		return 7000;
	}
	
	@Override
	public void doStuff() {
		super.doStuff();
		listener.changeSuperSnake(true);
	}
	
	@Override
	public void undoStuff() {
		super.undoStuff();
		listener.changeSuperSnake(false);
	}
	
	@Override
	protected int timeDoStuffOnHit() {
		return 25000;
	}
}
