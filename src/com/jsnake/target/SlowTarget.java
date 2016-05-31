package com.jsnake.target;


public class SlowTarget extends Target {

	public SlowTarget() {
	}
	
	@Override
	public int recalculateScore(int currentScore) {
		return currentScore;
	}

	@Override
	public int resizeSnake(int currentSnakeSize) {
		return currentSnakeSize;
	}

	@Override
	public int calcDelayOnHit() {
		return +20;
	}
	
	@Override
	public TargetType getTargetType() {
		return TargetType.SLOW;
	}
	
	@Override
	public int timeAlive() {
		return 8000;
	}
	
	@Override
	protected int timeDoStuffOnHit() {
		return 0;
	}
}
