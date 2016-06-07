package com.jsnake.target;

public class BombTarget extends Target {

	public BombTarget(TargetHitAction targetHitActionListener) {
		super.addTargetHitActionListener(targetHitActionListener);
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
		return 0;
	}

	@Override
	public int timeAlive() {
		return 8000;
	}

	@Override
	protected int timeDoStuffOnHit() {
		return 1;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.BOMB;
	}
	
	@Override
	protected void doStuff() {
		super.doStuff();
		targetHitActionListener.doGameOver();
	}

}
