import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * 
 * @project Lab 4 Localization 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 06/22/2013
 * 
 * Object Recognition Two (for part 2)
 * 
 *  The class is used to differentiate between the styrofoam block and wood block
 * 
 */

public class ObjectRecognitionTwo implements TimerListener {

	/*
	 * Objects to interact with other classes
	 */
	private ColorSensor ls;
	private UltrasonicPoller usPoller;
	private TwoWheeledRobot robot;
	private Navigation nav;
	private Odometer odo;
	private Timer oroTimer;
	private boolean objectDetected;

	//light sensor calibration value -> ambient light returned
	private int lsCalibration = -1;
	// true if the type has been determined
	private boolean typeDetected = false;
	// true if the robot is detecting in the timeOut method
	private boolean detecting = false;
	// true if the robot has the styrofoam block 
	private boolean haveStyrofoam = false;
	//true if lineing up the robot in inspect2()
	private boolean inspecting = false;
	
	/**
	 * object recongnition two constructor
	 * @param ls
	 * @param usPoller
	 * @param robot
	 * @param display
	 * @param odo
	 */
	public ObjectRecognitionTwo(ColorSensor ls, UltrasonicPoller usPoller, TwoWheeledRobot robot, Odometer odo) {
		this.ls = ls;
		this.usPoller = usPoller;
		this.robot = robot;
		this.objectDetected = false;
		this.odo = odo;
		this.nav = new Navigation(odo);
		
		//set timer to 40 ms
		this.oroTimer = new Timer(40, this);
		//set volume level
		Sound.setVolume(60);
	}
	
	/**
	 * returns the current light sensor value referenced to the calibration point
	 * @return
	 */
	public int getLSValue() {
		return ls.getRawLightValue() - lsCalibration;
	}
	
	
	/**
	 * TimerListner loop
	 * 
	 * This method will cause the robot to travel forward if there is an object within 40cm and it will use the light sensor to reach an appropriate distance to check if the block is wood or styrofoam
	 */
	public void timedOut() {
		
		/*
		 * make sure the floodlight is set to red
		 */
		if(ls.getFloodlight() != 0) {
			ls.setFloodlight(0);
			
			//In cases when the floodlight fails to change right away
			while(ls.getRawLightValue() == -1) {
				try { Thread.sleep(50); } catch (InterruptedException e) {}
			}
		}
		
		// poll the us sensor for the new reading
		int newUSValue = usPoller.filterUS();
		
		/*
		 * the object has been detected (us reading of less than 40cm)
		 */
		if(objectDetected) {
			/*
			 * if us > 40, we probably are not lined up correctly
			 * 
			 * all variables will be reset and the timer is stopped
			 * this will return to the method that called this one (inspect2(), which will then line up the robot) 
			 */
			if(newUSValue > 40) {
				objectDetected = false;
				typeDetected = false;
				robot.setSpeeds(0, 0);
				detecting = false;
				stop();
			/*
			 * if the type has not yet been detected, move forward so that a good light sensor reading can be taken
			 */
			} else if(!typeDetected) {
				
				// retrieve the current light value
				int lsValue = ls.getRawLightValue();
				
				/*
				 * if the difference is less than 5 the robot is far from the object, approach faster
				 */
				if(lsValue-lsCalibration < 5) {
					robot.setSpeeds(2.5,0);
				} 
				/*
				 * the robot is close, slow down
				 */
				else if(lsValue-lsCalibration < 40) {
					robot.setSpeeds(1.5, 0);
				} 
				/*
				 * the robot is close enough to take accurate light reading and make a decision
				 */
				else {
					
					//stop moving
					robot.setSpeeds(0, 0);
					//stop the timer
					oroTimer.stop();
					
					//store the reading with the red flood light on
					int redValue = lsValue;
					
					// set the floodlight to blue
					ls.setFloodlight(2);
					
					//wait till the floodlight is actually changed to blue
					while(ls.getRawLightValue() == -1) {
						try { Thread.sleep(50); } catch (InterruptedException e) {}
					}
					
					//store the reading with the blue flood light on
					int blueValue = ls.getRawLightValue();
					
					// if the difference between the red and blue is greater than 25, it is a wood block
					// actually difference is ~100
					if (redValue - blueValue > 25) {
					
						Sound.beep();
						Sound.buzz();
						
						//backup 10cm
						nav.backup(10);
					} 
					/*
					 * otherwise it is a styrofoam block
					 * difference between red and blue for styrofoam is <5
					 */
					else {
						
						Sound.beep();
						haveStyrofoam = true;
						//capture the styrofoam block
						capture();
						
					}
					
					// reset the floodlight to red
					ls.setFloodlight(0);
					//ensure it is actually red before continuing
					while(ls.getRawLightValue() == -1) {
						try { Thread.sleep(50); } catch (InterruptedException e) {}
					}
					
					//reset variables to exit inspecting loops
					typeDetected = false;
					inspecting = false;
					detecting = false;
					
				}
			}
		}
		/*
		 * the US is reading a value < 40 and block has not been detected -> block detectee
		 */
		else if(newUSValue < 40) {
			objectDetected = true;
		}
			
	}
	
	/*
	 * starts the timer for the timeOut loop
	 */
	public void start() {
		//we are detecting
		detecting = true;
		
		//starts the ultrasonic poller timer
		usPoller.start();
		
		//set floodlight to red
		ls.setFloodlight(0);
		
		//calibrates the light sensor (retrieves ambient light value)
		while (lsCalibration < 0) {
			lsCalibration = ls.getRawLightValue();
			try { Thread.sleep(50); } catch (InterruptedException e) {}
		}
		
		//start the timer
		oroTimer.start();

	}
	
	/*
	 * stop the timer
	 */
	public void stop() {
		oroTimer.stop();
	}
	
	/**
	 * Inspect Method drives the robot to about 15cm from the object, 
	 * calls inspect2() to inspect the object, 
	 * and then if the object is not the styrofoam block
	 * returns the robot to its original position
	 * 
	 * @param yStart
	 * @param yEnd
	 * @param distanceToBlock
	 */
	public void inspect(int yStart, int yEnd, int distanceToBlock) {
		
		//store starting coordinates
		double xOriginal = odo.getX();
		double yOriginal = odo.getY();
		
		//backup to the middle of the object
		nav.backup(Math.abs(yEnd - yStart) / 2 );
		
		//turn to face the object
		nav.turnTo(180);
		usPoller.rotateUS(170);
		
		//scan the object for the distance
		int distanceToObject = usPoller.scan();
		
		//take the minimum of the distance to the object as reported by the findStyrofoam loop or the one just measured
		if(distanceToObject > distanceToBlock + 5) {
			distanceToObject = distanceToBlock;
		}
		
		//travel to the object - 15cm
		nav.travelTo(odo.getX() - distanceToObject + 15, odo.getY());
		
		//line up the robot to the object and inspect to see if it is styrofoam or wood
		inspect2(180);
		
		// if the object was a wood block return to the original coordinates to continue looking for blocks
		if(!getHaveStyrofoam()) {
			nav.travelTo(xOriginal, yOriginal);
			nav.turnTo(90);
			usPoller.rotateUS(180);
		}
		
	}
	
	/**
	 * inspect2 will line up the robot to the block and
	 * then start the timeOut loop to determine what the object is
	 * @param heading
	 */
	public void inspect2(int heading) {
		inspecting  = true;
		
		/*
		 * main loop
		 */
		while(inspecting) {
			
			//measure the distance to the object at 3 offsets left, center, right
			int angleAValue;
			int angleBValue;
			int angleCValue;
			
			nav.turnTo(heading + 40);
			usPoller.rotateUS(heading + 10);
			angleAValue = usPoller.scan();
			
			nav.turnTo(heading);
			usPoller.rotateUS(heading);
			angleBValue = usPoller.scan();
			
			nav.turnTo(heading - 40);
			usPoller.rotateUS(heading);
			angleCValue = usPoller.scan();
			
			/*
			 * if oriented along the y axis, 
			 * adjust the robots x position
			 */
			if (Math.abs(Math.sin(Math.toRadians(heading))) > 0.95) {
				//initialize current x
				int x = (int) odo.getX();
				//if the reading to the right was bigger than the left move to the left
				if(angleCValue - angleAValue > 10 ) {
					x -= 5;
				} 
				// if the reading on the left was bigger than the reading on the right move to the right
				else if (angleCValue - angleAValue < -10 ) {
					x+= 5;
				}
				//travel to adjusted position				
				nav.travelTo(x, odo.getY());
			} 
			/*
			 * if oriented along the x axis
			 * adjust the robots y position
			 */
			else {
				//initialize current y position
				int y = (int) odo.getY();
				
				// if the reading to the right is was bigger than the reading to the left, go to the left
				if(angleCValue - angleAValue > 10 ) {
					y -= 5;
				} 
				// if the reading to the left was bigger than the reading to the right, go right
				else if (angleCValue - angleAValue < -10 ) {
					y+= 5;
				}
				
				//travel to adjusted position			
				nav.travelTo(odo.getX(), y);
			}
			
			//correct the heading of the robot and ultrasonic
			nav.turnTo(heading);
			usPoller.rotateUS(heading);
			
			/*
			 * if the us reading is less than 40, the robot is good to move on to the light sensor part of object recognition
			 */
			if(usPoller.scan() < 40) {
				usPoller.rotateUS(heading-10);
				//start the timeOut timer -> do light sensor recognition
				start();
				
				//while still detecting wait
				while(detecting) {
					try{
						Thread.sleep(200);
					} catch(Exception e) {
						
					}
				}
			}
		}

	}
	
	/**
	 * returns true if the styrofoam block has been captured
	 * @return
	 */
	public boolean getHaveStyrofoam() {
		return haveStyrofoam;
	}
	
	
	/**
	 * Attempt to capture the styrofoam block
	 */
	public void capture() {
		//travel forward 5cm
		nav.travelTo(odo.getX() - 5, odo.getY());
		//turn around to scoop the block
		nav.turnTo(330);
		//travel to the end destination
		nav.travelTo(odo.getX(), 165);
		nav.travelTo(70, 190);
	}
	
}
