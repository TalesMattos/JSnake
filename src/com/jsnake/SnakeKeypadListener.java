package com.jsnake;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SnakeKeypadListener extends KeyAdapter {

	private KeypadSnake keyPressed = KeypadSnake.NULL;
	
	public void configure() {
		keyPressed = KeypadSnake.NULL;
	}
	
	public KeypadSnake getKeyPressed() {
		return keyPressed;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (   key != KeyEvent.VK_LEFT
				&& key != KeyEvent.VK_RIGHT 
				&& key != KeyEvent.VK_UP 
				&& key != KeyEvent.VK_DOWN )
			return;
		
		if (   key == KeyEvent.VK_LEFT && KeypadSnake.LEFT.equals(keyPressed)
				|| key == KeyEvent.VK_LEFT && KeypadSnake.RIGHT.equals(keyPressed)
				|| key == KeyEvent.VK_RIGHT && KeypadSnake.RIGHT.equals(keyPressed)
				|| key == KeyEvent.VK_RIGHT && KeypadSnake.LEFT.equals(keyPressed)
				|| key == KeyEvent.VK_UP && KeypadSnake.UP.equals(keyPressed)
				|| key == KeyEvent.VK_UP && KeypadSnake.DOWN.equals(keyPressed)
				|| key == KeyEvent.VK_DOWN && KeypadSnake.DOWN.equals(keyPressed)
				|| key == KeyEvent.VK_DOWN && KeypadSnake.UP.equals(keyPressed))
			return;
		
		if (key == KeyEvent.VK_LEFT) {
			keyPressed = KeypadSnake.LEFT;
		} else if (key == KeyEvent.VK_RIGHT) {
			keyPressed = KeypadSnake.RIGHT;
		} else if (key == KeyEvent.VK_UP) {
			keyPressed = KeypadSnake.UP;
		} else	if (key == KeyEvent.VK_DOWN) {
			keyPressed = KeypadSnake.DOWN;
		}
		
	}
	
	public enum KeypadSnake {
		UP, DOWN, RIGHT, LEFT, NULL; 
	}
}
