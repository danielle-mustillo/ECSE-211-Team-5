package services;

import hardwareAbstraction.*;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.*;


/**
 * This class takes hardwareAbstractions and prints them to screen.
 * Currently supports line poller and USPoller. 
 * Easily extended to support colorPoller.
 * Will be removed for final project.
 * 
 * THE CALL IN SERVICE MANAGER MUST BE REMOVED WHEN FINAL PROJECT IS TO BE HANDED IN.
 * @author danielle
 *
 */
public class HardwareViewer implements TimerListener {
	public UltrasonicPoller usp;
//	public LinePoller lp;
	private int pollRate;
	private Timer refresher;

	public HardwareViewer(Manager manager) {
		
		//get hardware
		this.usp = manager.hm.ultrasonicPoller;
//		this.lp = manager.hm.linePoller;

	}
	
	@Override
	public void timedOut() {
		printUS();
//		printLP();
	}
	
	public void start(int pollRate) {
		this.refresher = new Timer(pollRate, this);
		this.refresher.start();
	}
	
	public void stop() {
		this.refresher.stop();
		this.refresher = null;
	}

	private void printUS() {
		int left = 0;
		int center = 1;
		int right = 2; 
		int usL = usp.getUSReading(left);
		int usC = usp.getUSReading(center);
		int usR = usp.getUSReading(right);
		RConsole.println(usL + ";" + usC + ";" + usR);
	}
	
//	private void printLP() {
//		int[][] readings = lp.readings;
//		int left = 0;
//		int right = 1;
//		int lpL = readings[left][0];
//		int lpR = readings[right][0];
//		RConsole.println("LP readings for LEFT and RIGHT");
//		RConsole.println("\tLeft: " + lpL);
//		RConsole.println("\tRight: " + lpR);
//	}
	
}
