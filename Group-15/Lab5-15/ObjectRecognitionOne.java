import lejos.nxt.ColorSensor;
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
 * Object Recognition One (for part 1)
 * 
 *  The class is used to differentiate between the styrofoam block and wood block
 * 
 */

public class ObjectRecognitionOne implements TimerListener {

	private ColorSensor ls;
	private UltrasonicPoller usPoller;
	private TwoWheeledRobot robot;
	private LCDInfo display;
	private Timer oroTimer;
	private int lastUSValue;
	private boolean objectDetected;

	private LCDInfo.BlockType object;
	private int lsCalibration = -1;
	private boolean typeDetected = false;
	
	/**
	 * Object Recognition One Constructor
	 * @param ls
	 * @param usPoller
	 * @param robot
	 * @param display
	 */
	public ObjectRecognitionOne(ColorSensor ls, UltrasonicPoller usPoller, TwoWheeledRobot robot, LCDInfo display) {
		this.ls = ls;
		this.usPoller = usPoller;
		this.robot = robot;
		this.display = display;
		this.lastUSValue = 256;
		this.objectDetected = false;
		this.oroTimer = new Timer(40, this);
		ls.setFloodlight(0);
	}
	
	/**
	 * TimerListner loop
	 * 
	 * This method will cause the robot to travel forward if there is an object within 40cm and it will use the light sensor to reach an appropriate distance to check if the block is wood or styrofoam
	 */
	public void timedOut() {
		
		/*
		 * ensures the ultrasonic poller has started
		 */
		if(lastUSValue < 256) {
			//retrieve new ultrasonic poller value
			int newUSValue = usPoller.filterUS();
		
			/*
			 * if an object was detected earlier
			 */
			if(objectDetected) {
				/*
				 * if the us sensor reports a distance of more than 40, we no longer see the object
				 */
				if(newUSValue > 40) {
					//reset variables and the lcd display + stop the robot from moving forward
					objectDetected = false;
					display.blockDetected(false);
					display.type(LCDInfo.BlockType.NONE);
					typeDetected = false;
					robot.setSpeeds(0, 0);
				} 
				/*
				 * the robot still sees the object and the type has not yet been distinguished
				 */
				else if(!typeDetected) {
					
					//get the current light sensor reading
					int lsValue = ls.getRawLightValue();
					
					/*
					 * if the difference is less than 5 the robot is far from the object, approach faster
					 */
					if(lsValue-lsCalibration < 5) {
						robot.setSpeeds(6,0);
					} 
					
					/*
					 * if the difference 5 < d < 10 the robot is nearing the object, approach at half the speed
					 */
					else if(lsValue-lsCalibration < 10) {
						robot.setSpeeds(3, 0);
					} 
					/*
					 * if the difference is 10 < d < 20 the robot is close to the object, approach slowly
					 */
					else if(lsValue-lsCalibration < 20) {
						robot.setSpeeds(1.5, 0);
					} 
					/*
					 * the robot is close enough to take accurate light reading and make a decision
					 */
					else {
						//stop moving
						robot.setSpeeds(0, 0);
						
						//stop the timeOut timer
						oroTimer.stop();
						
						//store the light sensor value when the floodlight is red
						int redValue = lsValue;
						
						//change the floodlight to blue
						ls.setFloodlight(2);
						
						//ensures the floodlight is set to blue before continuing
						while(ls.getRawLightValue() == -1) {
							try { Thread.sleep(50); } catch (InterruptedException e) {}
						}
						
						//store the light sensor value when the floodlight is blue
						int blueValue = ls.getRawLightValue();
						
						// if the difference between the red and blue is greater than 25, it is a wood block
						// actually difference is ~100
						if (redValue - blueValue > 25) {
							object = LCDInfo.BlockType.WOOD;
						} 
						/*
						 * otherwise it is a styrofoam block
						 * difference between red and blue for styrofoam is <5
						 */
						else {
							object = LCDInfo.BlockType.STYROFOAM;
						}
						
						// show the type of block on the LCD
						display.type(object);
						
						//reset the the floodlight to red
						ls.setFloodlight(0);
						
						//ensures the floodlight is red
						while(ls.getRawLightValue() == -1) {
							try { Thread.sleep(50); } catch (InterruptedException e) {}
						}	
						
						//type is detected
						typeDetected = true;
						
						//start timeOut timer to check when the block is removed and when a new one is placed in front
						oroTimer.start();
						
					}
				}
			}
			/*
			 * object is near, determine its type
			 */
			else if(newUSValue < 40) {
				
				objectDetected = true;
				//show that a block was detected on the LCD
				display.blockDetected(true);
			}
			
		} 
		/*
		 * only for the inital running | makes sure the usPoller has started
		 */
		else {
			lastUSValue = usPoller.filterUS();
			
		}
	}
	
	/**
	 * starts the timer for the timeOut loop
	 */
	public void start() {
				
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
	
	/**
	 * stops the timeOut timer
	 */
	public void stop() {
		oroTimer.stop();
	}
	
	
	
}
