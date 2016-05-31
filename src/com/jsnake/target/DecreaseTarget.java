package com.jsnake.target;


public class DecreaseTarget extends Target {

	public DecreaseTarget() {
	
	}
	
	@Override
	public int recalculateScore(int currentScore) {
		return currentScore;
	}

	@Override
	public int resizeSnake(int currentSnakeSize) {
		if (currentSnakeSize > 3 )
			return --currentSnakeSize;
		return currentSnakeSize;
	}

	@Override
	public int calcDelayOnHit() {
		return 0;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.DECREASE;
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
