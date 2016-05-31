package com.jsnake.target;


public class AppleTarget extends Target {

	public AppleTarget() {
		
	}
	
	@Override
	public int recalculateScore(int currentScore) {
		return (currentScore + 3);
	}

	@Override
	public int resizeSnake(int currentSnakeSize) {
		return ++currentSnakeSize;
	}

	@Override
	public int calcDelayOnHit() {
		return -5;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.APPLE;
	}
	
	@Override
	public int timeAlive() {
		return 10000;
	}
	
	@Override
	protected int timeDoStuffOnHit() {
		return 0;
	}
}
