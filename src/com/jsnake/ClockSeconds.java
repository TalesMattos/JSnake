package com.jsnake;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class ClockSeconds {

	private ClockSecondsListener listener;
	private int seconds;
	private int secondsTotal;
	private boolean increment = true;
	private boolean running;
	private Timer timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (increment) {
				++seconds;
				if (secondsTotal > 0 && seconds >= secondsTotal)
					timeout();
			} else {
				--seconds;
				if (secondsTotal > 0 && seconds <= 0)
					timeout();				
			}
		}
	});
	
	public ClockSeconds() {
	}

	public ClockSeconds(ClockSecondsListener listener) {
		addListener(listener);
	}

	public ClockSeconds(int secondsTotal, boolean increment, ClockSecondsListener listener) {
		this(secondsTotal, increment);
		addListener(listener);
	}
	
	public ClockSeconds(int secondsTotal, boolean increment) {
		if (secondsTotal < 0)
			throw new IllegalArgumentException("secondsTotal não pode ser menor que zero");
		this.secondsTotal = secondsTotal;
		this.increment = increment;
		if (!increment)
			this.seconds = secondsTotal;
	}
	
	public void addListener(ClockSecondsListener listener) {
		this.listener = listener;
	}

	private void reset() {
		running = false;
		if (increment)
			this.seconds = 0;
		else
			this.seconds = secondsTotal;
		timer.stop();		
	}
	
	public void start() {
		running = true;
		timer.start();
	}
	
	public void stop() {
		reset();
	}

	public void timeout() {
		timer.stop();
		if (listener != null);
			listener.timeout();
		reset();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public int getSeconds() {
		return seconds;
	}
	
	public void setIncrement(boolean increment) {
		this.increment = increment;
	}
	
	@Override
	public String toString() {
		int minutos = seconds / 60;
		int segundos = seconds % 60;
		return (increment ? "" : "-") + (seconds == 0 || minutos < 10 ? "0" : "") + minutos 
														+ ":" + (segundos < 10 ? "0" : "") + segundos;
	}
	
}
