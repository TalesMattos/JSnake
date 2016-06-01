package com.jsnake;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.DecimalFormat;

public class ControlPanel {

	private boolean drawGameOver;
	private ClockSeconds timerClockIncrement;
	private int score;
	private int countHitTarget;
	private int snakeSize;
	private int snakeDelay;
	private boolean superSnake;
	
	public ControlPanel() {
		configure();
	}
	
	public int getSnakeDelay() {
		return snakeDelay;
	}
	
	public void setSnakeDelay(int snakeDelay) {
		this.snakeDelay = snakeDelay;
	}
	
	public int getSnakeSize() {
		return snakeSize;
	}
	
	public void setSnakeSize(int snakeSize) {
		this.snakeSize = snakeSize;
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public int getCountHitTarget() {
		return countHitTarget;
	}
	
	public void incrementCountHitTarget() {
		countHitTarget++;
	}
	
	public void configure() {
		snakeDelay = JSnake.MAX_DELAY;
		snakeSize = 3;
		score = 0;
		countHitTarget = 0;
		drawGameOver = false;
		if (timerClockIncrement == null) {
			timerClockIncrement = new ClockSeconds(); 
		}
		timerClockIncrement.stop();
	}
	
	public boolean isDrawGameOver() {
		return drawGameOver;
	}
	
	private String[] getInfo() {
		String clockInfo = "Timer: " + timerClockIncrement.toString();
		String scoreInfo = "Score: " + score; 
		String countInfo = "Items: " + countHitTarget;
		String speedInfo = "Speed: " + (new DecimalFormat("000").format(((float)JSnake.MAX_DELAY / (float)snakeDelay) * 100)); //FIXME - melhorar 
		String sizeInfo = "Size: " + snakeSize;
		return new String[] {clockInfo, scoreInfo, speedInfo, sizeInfo, countInfo};
	}
	
	public void gameOver(Graphics g) {
		drawGameOver = true;
		String infos[] = getInfo();
		timerClockIncrement.stop();
		String msg = "GAME OVER!";
		Font fontM = new Font("Courrier", Font.BOLD, 18);
		FontMetrics metrica = g.getFontMetrics(fontM);
		g.setColor(Color.BLUE);
		g.setFont(fontM);
		g.drawString(msg, (JSnake.PANEL_WIDTH - metrica.stringWidth(msg)) / 2, JSnake.PANEL_HEIGHT / 3);
		
		Font fontP = new Font("Courrier", Font.BOLD, 14);
		FontMetrics metricaP = g.getFontMetrics(fontP);
		g.setColor(Color.BLACK);
		g.setFont(fontP);
		int count = 0;
		for (String info : infos) {
			g.drawString(info, (JSnake.PANEL_WIDTH - metricaP.stringWidth(info)) / 2, (JSnake.PANEL_HEIGHT / 2) + (++count * 20));
		}
	}
	
	public void drawInfo(Graphics g) {
		String infoText = "";
		for (String info : getInfo())
			infoText += (info + "        ");
		drawInfo(g, infoText, JSnake.COORDINATE_SIZE, JSnake.PANEL_HEIGHT - JSnake.COORDINATE_SIZE);
	}
	
	public void startClock() {
		if (!timerClockIncrement.isRunning())
			timerClockIncrement.start();
	}
	
	public boolean isSuperSnake() {
		return superSnake;
	}
	
	public void setSuperSnake(boolean superSnake) {
		this.superSnake = superSnake;
	}
	
	public void drawInfo(Graphics g, String info, int xPos, int yPos) {
		Font infoFont = new Font("Courrier", Font.BOLD, 12);
		g.setColor(Color.BLUE);
		g.setFont(infoFont);
//		FontMetrics infoMetric = g.getFontMetrics(infoFont);
//		g.drawString(info, (JSnake.PANEL_WIDTH - infoMetric.stringWidth(info)) - JSnake.COORDINATE_SIZE, JSnake.PANEL_HEIGHT - JSnake.COORDINATE_SIZE);
		g.drawString(info, xPos, yPos);
	}
	
}
