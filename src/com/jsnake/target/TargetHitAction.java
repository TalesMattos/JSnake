package com.jsnake.target;

import com.jsnake.SnakeGame;

public interface TargetHitAction {

	void addSnakeGameListener(SnakeGame listener);
	void doStuff();
	void undoStuff();
	
}
