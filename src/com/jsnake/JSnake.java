package com.jsnake;

import javax.swing.JFrame;

/**
 * 
 * @author tales-mattos (30/05/2016)
 *
 */
public class JSnake extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final int PANEL_WIDTH = 400;
	public static final int PANEL_HEIGHT = 400;
	public static final int COORDINATE_SIZE = 10;
	public static final int ALL_COORDINATES = (PANEL_WIDTH / COORDINATE_SIZE) * (PANEL_HEIGHT /COORDINATE_SIZE);
	public static final int MIN_SPPED = 150;
	public static final int MAX_SPPED = 20;
	
	public JSnake() {
		add(new SnakeGame());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 440);
		setLocationRelativeTo(null);
		setTitle("JSnake");
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		new JSnake();
	}

}