package com.jsnake.target;


public class FlyTarget extends Target {

	public FlyTarget() {

	}
	
	@Override
	public int recalculateScore(int currentScore) {
		return ++currentScore;
	}

	@Override
	public int resizeSnake(int currentSnakeSize) {
		return ++currentSnakeSize;
	}

	@Override
	public int calcDelayOnHit() {
		return -10;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.FLY;
	}
	
	@Override
	public int timeAlive() {
		return 0;
	}
	
	@Override
	protected int timeDoStuffOnHit() {
		return 0;
	}
}
