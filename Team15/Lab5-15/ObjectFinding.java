import lejos.nxt.comm.RConsole;

/**
 * 
 * @project Lab 4 Localization 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 06/22/2013
 * 
 * Object Finding
 * 
 *  Contained within this class is the main loop for traversing the course while looking for the styrofoam block
 *  
 *  Initially a different approach was taken with regards to lab 5.  That method was to map the enclosure as it looked for the styrofoam block
 *  this was eliminate due to time constraints (because of the previous strategies failing).    
 * 
 */
public class ObjectFinding {
	
	//Objects to interact with other classes
	private UltrasonicPoller usPoller;
	private Odometer odo;
	private ObjectRecognitionTwo ort;
	private Navigation nav;
	
	/**
	 * Constructor
	 * @param usPoller
	 * @param odo
	 * @param ort
	 */
	public ObjectFinding(UltrasonicPoller usPoller, Odometer odo, ObjectRecognitionTwo ort) {
		this.usPoller = usPoller;
		this.odo = odo;
		this.ort = ort;
		this.nav = odo.getNavigation();

	}
	
	
	
	/**
	 * Find styrofoam loop
	 * 
	 * This method could have been implemented in a better manner, however, due to time constraints it was not possible
	 */
	public void findStyrofoam() {
		
		//travel to the start coordinates
		nav.travelTo(70, 30);
		
		/*
		 * Continue running until the robot has found the styrofoam
		 */
		while(!ort.getHaveStyrofoam()) {
			
			/*
			 * Scans for objects ahead to know when to stop looking to the side (avoid crashing)
			 * 
			 * checks in 3 different offsets (left, center, right) in case the block is not directly ahead
			 * 
			 */
			nav.turnTo(20);
			usPoller.rotateUS(90);
			int d1 = usPoller.scan();
			
			nav.turnTo(90);
			usPoller.rotateUS(90);
			int d2 = usPoller.scan();
			
			nav.turnTo(110);
			usPoller.rotateUS(90);
			int d3 = usPoller.scan();
			
			//return to previous orientation
			nav.turnTo(90);
			final int distanceToBlock;
			//intiliaze yStart
			int yStart = (int) odo.getY();
			
			//choose the smallest distance obtained
			if( d1 < d2 && d1 < d3) {
				distanceToBlock = d1+yStart;
			} else if( d2 < d3 && d2 < d1) {
				distanceToBlock = d2+yStart;
			} else {
				distanceToBlock = d3+yStart;
			}
			
			//RConsole.println("distance to block " + String.valueOf(distanceToBlock));
			
			//initialize variables
			int usLastValue = 256;
			int usCurrentValue;
			double y = odo.getY();
			
			int yEnd;
			boolean start = false;

			//rotate the ultrasonic to look to the left  
			usPoller.rotateUS(180);
			usPoller.clear();
			
			//start polling with timerlistner
			usPoller.start();
			
			
			//travel to the block ahead - 15cm
			(new Thread() {
				public void run() {
					nav.travelTo(odo.getX(), distanceToBlock - 15);
				}
			}).start();
		
			/*
			 * look to the left until we reach the block in front of us - 15cm
			 */
			while (y < distanceToBlock - 16) {
				
				//retrieve what the ultrasonic sensor is reading
				usCurrentValue = usPoller.filterUS();
				
				//update the y coordinate
				y = odo.getY();
				//RConsole.println(String.valueOf(usCurrentValue));
				
				//don't do anything if this is the first time running (firsttime usLastValue ==256)
				if(usLastValue != 256) {
					
					/*
					 * we are reading something closer than before, and we have not detected a rising edge yet
					 * thus the robot has found the first edge of the block
					 */
					if((usCurrentValue - usLastValue < -15) && !start) {
						//store the starting y
						yStart = (int) odo.getY();
						start = true;
						//RConsole.println(String.valueOf(usCurrentValue) + ";" + String.valueOf(usLastValue));
					} 
					/*
					 * we are reading something farther than before, and we have detected a rising edge
					 * thus the robot has found the last edge of the block
					 */
					else if((usLastValue - usCurrentValue < -15 || usLastValue == 255 ) && usCurrentValue != 255 && start) {
						//store end coordinate
						yEnd = (int) odo.getY();
						//stop polling values
						usPoller.stop();
						//stop going forward, so we can inspect the block we have just found
						nav.stop();
						
						//Sleep for a little while to ensure navigation stops
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//go to inspect method, which will inspect the block
						ort.inspect(yStart, yEnd, usLastValue);
						
						//in case the block was not the styrofoam
						if(!ort.getHaveStyrofoam()) {
							
							/*
							 * reset to detect the next obstacle
							 */
							start = false;
							usLastValue = 256;
							
							//resume traveling to the block in front of us
							(new Thread() {
								public void run() {
									nav.travelTo(odo.getX(), distanceToBlock -15);
								}
							}).start();
						}
					}
					
				}
				
				//update usLastValue for the next iteration
				usLastValue = usCurrentValue;
				
				//Make sure we don't run to often
				try {
					Thread.sleep(35);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			/*
			 * when we reach the block in front of us inspect it
			 */
			
			nav.turnTo(90);
			usPoller.rotateUS(90);
			
			//inspect the block
			ort.inspect2(90);
			boolean blocked = true;
			
			/*
			 * this will move the robot to the left by one tile until the tile in front of it (at heading of 90deg) is clear
			 * 
			 * one imporvement that was not implemented would be to move the robot back to its inital x position after the block in front had been cleared
			 */
			while(blocked) {
				//travel to one tile over
				nav.travelTo(odo.getX() - 30, odo.getY());
				
				/*
				 * inspect tile in front
				 */
				nav.turnTo(90);
				
				int angleAValue;
				int angleBValue;
				int angleCValue;
				
				//scan at three offsets to check left, middle, right
				nav.turnTo(90 + 30);
				usPoller.rotateUS(90 + 15);
				angleAValue = usPoller.scan();
				nav.turnTo(90);
				usPoller.rotateUS(90);
				angleBValue = usPoller.scan();
				nav.turnTo(90 - 40);
				usPoller.rotateUS(90);
				angleCValue = usPoller.scan();
				
				//if the values report back as all greater than 60 we can travel in the +y direction again and continue the scanning
				if(angleAValue > 60 && angleBValue > 60 && angleCValue > 60) {
					blocked = false;
					break;
				}				
				
			}
			
			//returns to first loop
		}		
		
		//Done
		//RConsole.println("Done");
	}

}
