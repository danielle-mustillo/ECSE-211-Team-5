package slave;

import hardwareAbstraction.RemoteCommands;
import hardwareAbstraction.UltrasonicPoller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import slave.RemoteUltrasonicPoller.USPState;
import lejos.nxt.*;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.RS485;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

/**
 * Enables remote control of a motor object using RS485. 
 **/
public class NXTRemoteControl extends Thread implements RemoteCommands, RegulatedMotorListener {

   private DataInputStream dis = null;
   private DataOutputStream dos = null;
   
   //private DataInputStream lisdis = null;
   //private DataOutputStream lisdos = null;
   
   private NXTCommConnector connector;
   private NXTConnection con;
   
   
   //private NXTConnection listenersCon;
   private int id;
   private int command;
   private int tachoCount;
   private boolean isMoving;
   private int speed;
   private float maxSpeed;
   private int acceleration;
   private int angle;
   private boolean immediateReturn, isStalled;

   private static NXTRegulatedMotor A = null;
   private static NXTRegulatedMotor B = null;
   private static NXTRegulatedMotor C = null;
   private static RemoteUltrasonicPoller usp = null;

   public NXTRemoteControl(MotorPort portA) {
      
      A = new NXTRegulatedMotor(portA);

}
   public NXTRemoteControl(MotorPort portA, MotorPort portB) {
      
         A = new NXTRegulatedMotor(portA);
         B = new NXTRegulatedMotor(portB);
         

   }
   public NXTRemoteControl(MotorPort portA, MotorPort portB,
         MotorPort portC) {
      
         A = new NXTRegulatedMotor(portA);
         B = new NXTRegulatedMotor(portB);
         C = new NXTRegulatedMotor(portC);

   }
   
   public NXTRemoteControl(MotorPort portA, MotorPort portB, MotorPort portC, RemoteUltrasonicPoller usp) {
	   A = new NXTRegulatedMotor(portA);
       B = new NXTRegulatedMotor(portB);
       C = new NXTRegulatedMotor(portC);
       usp = new RemoteUltrasonicPoller();
   }

   protected void executeCommand(int id, int command) throws IOException {

		NXTRegulatedMotor motor = getMotor(id);
		RemoteUltrasonicPoller usp = getSensor(id);
		
		if (usp != null) {
			switch( command) {
			case PING_CENTER: {
				RConsole.println("PING_CENTER");
				usp.setUSPState(USPState.PING_CENTER);
			}
			case PING_LEFT: {
				RConsole.println("PING_LEFT");
				usp.setUSPState(USPState.PING_LEFT);
			}
			case PING_RIGHT: {
				RConsole.println("PING_RIGHT");
				usp.setUSPState(USPState.PING_RIGHT);
			}
			case PING_ALL: {
				RConsole.println("PING_ALL");
				usp.setUSPState(USPState.PING_ALL);
			}
			}
		}
		else {
			switch (command) {

			case FORWARD: {
				RConsole.println("FORWARD");
				motor.forward();
				break;
			}

			case BACKWARD: {
				RConsole.println("BACKWARD");
				motor.backward();
				break;
			}

			case ROTATE: {
				RConsole.println("ROTATE");
				angle = dis.readInt();
				RConsole.println("angle" + angle);
				immediateReturn = dis.readBoolean();
				RConsole.println("immediateReturn" + immediateReturn);
				motor.rotate(angle, immediateReturn);
				break;
			}

			case STOP: {
				RConsole.println("STOP");
				motor.stop();
				break;
			}

			case ROTATE_TO: {
				RConsole.println("ROTATE_TO");
				angle = dis.readInt();
				immediateReturn = dis.readBoolean();
				RConsole.println("angle" + angle);
				RConsole.println("immediateReturn" + immediateReturn);
				motor.rotateTo(angle, immediateReturn);
				break;
			}

			case FLT: {
				RConsole.println("FLT");
				motor.flt();
				break;
			}

			case GET_TACHO_COUNT: {
				RConsole.println("GET_TACHO_COUNT");
				tachoCount = motor.getTachoCount();
				RConsole.println("tachoCount" + tachoCount);
				dos.writeInt(tachoCount);
				dos.flush();
				break;
			}

			case IS_MOVING: {
				RConsole.println("IS_MOVING");
				isMoving = motor.isMoving();
				RConsole.println("isMoving" + isMoving);
				dos.writeBoolean(isMoving);
				dos.flush();
				break;
			}

			case SET_SPEED: {
				int speed;
				RConsole.println("SET_SPEED");
				speed = dis.readInt();
				RConsole.println("SET_SPEED=" + speed);
				motor.setSpeed(speed);
				break;
			}

			case SET_ACCELERATION: {
				RConsole.println("SET_ACCELERATION");
				acceleration = dis.readInt();
				RConsole.println("acceleration=" + acceleration);
				motor.setAcceleration(acceleration);

				break;
			}

			case GET_LIMIT_ANGLE: {
				RConsole.println("GET_LIMIT_ANGLE");
				angle = motor.getLimitAngle();
				RConsole.println("angle=" + angle);
				dos.writeInt(angle);
				dos.flush();
				break;
			}

			case RESET_TACHO_COUNT: {
				RConsole.println("RESET_TACHO_COUNT");
				motor.resetTachoCount();
				break;
			}

			case GET_SPEED: {
				RConsole.println("GET_SPEED");
				speed = motor.getSpeed();
				RConsole.println("speed=" + speed);
				dos.writeInt(speed);
				dos.flush();
				break;
			}

			case IS_STALLED: {
				RConsole.println("IS_STALLED");
				isStalled = motor.isMoving();
				RConsole.println("isStalled=" + isStalled);
				dos.writeBoolean(isStalled);
				dos.flush();
				break;

			}

			case GET_ROTATION_SPEED: {
				RConsole.println("GET_ROTATION_SPEED");
				speed = motor.getRotationSpeed();
				RConsole.println("speed=" + speed);
				dos.writeInt(speed);
				dos.flush();
				break;
			}

			case GET_MAX_SPEED: {
				RConsole.println("GET_MAX_SPEED");
				maxSpeed = motor.getMaxSpeed();
				RConsole.println("maxSpeed=" + maxSpeed);
				dos.writeFloat(maxSpeed);
				dos.flush();
				break;

			}
			case ADD_LISTENER: {
				motor.addListener(this);
				// if (listenersCon==null){
				// connectionThread connect=new connectionThread();
				// connect.start();
				// }

				break;

			}

			case SUSPEND_REGULATION: {
				boolean suspended;
				RConsole.println("SUSPEND_REGULATION");
				suspended = motor.suspendRegulation();
				dos.writeBoolean(suspended);
				dos.flush();
				break;

			}
			}
		}

   }

   protected RemoteUltrasonicPoller getSensor(int id) {

	      switch (id) {
	      case 4:
	         return usp;
	      }
	      return null;
	   }   
   protected NXTRegulatedMotor getMotor(int id) {

      switch (id) {
      case 1:
         return A;

      case 2:
         return B;

      case 3:
         return C;
      }

      return null;
   }   
   
   protected int getMotor(RegulatedMotor motor) {

      if(motor.equals(Motor.A))
         return 1;
      else  if(motor.equals(Motor.B))
         return 2;
      else  if(motor.equals(Motor.C))
         return 3;
      
      return -1;
   }
   
   public void run() {

      while (true) {
         
         // Wait for connection if not connected
         if (con == null) {
            LCD.drawString("waiting", 0, 2);

            connector = RS485.getConnector();
            con = connector.waitForConnection(0, NXTConnection.RAW);

            if (con != null) {
               dis = con.openDataInputStream();
               dos = con.openDataOutputStream();
            }
            LCD.drawString("Connected", 0, 2);
            LCD.refresh();
         }
         
         try {
            id = dis.readInt();
            command = dis.readInt();
            RConsole.println("id="+id +"command =" +command);
            executeCommand(id, command);

         } catch (IOException e) {
         //A.stop();
         //B.stop();
            //con.close();
            //con = null;
            //e.printStackTrace();
         }

      
   
      }
}
   @Override
   public void rotationStarted(RegulatedMotor motor, int tachoCount, boolean stalled,
         long timeStamp) {
      
//      try {
//         
//         int id=getMotor( motor);
//         lisdos.writeInt(id);
//         lisdos.writeInt(ROTATION_STARTED);
//         lisdos.writeInt(tachoCount);
//         lisdos.writeBoolean(stalled);
//         lisdos.writeLong(timeStamp);
//         lisdos.flush();
//      } catch (IOException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
      }
      
   
   @Override
   public void rotationStopped(RegulatedMotor motor, int tachoCount, boolean stalled,
         long timeStamp) {
      
//try {
//         
//         int id=getMotor(motor);
//         lisdos.writeInt(id);
//         lisdos.writeInt(ROTATION_STOPPED);
//         lisdos.writeInt(tachoCount);
//         lisdos.writeBoolean(stalled);
//         lisdos.writeLong(timeStamp);
//         lisdos.flush();
//      } catch (IOException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      }
         
   }
   
//   public class connectionThread extends Thread {
//      
//      public void run(){
//   
//         // Wait for connection if not connected
//         if (listenersCon == null) {
//            LCD.drawString("waiting", 0, 2);
//
//            connector = RS485.getConnector();
//            listenersCon = connector.waitForConnection(0, NXTConnection.RAW);
//
//            if (con != null) {
//               lisdis = con.openDataInputStream();
//               lisdos = con.openDataOutputStream();
//            }
//            LCD.drawString("Connected", 0, 2);
//            LCD.refresh();
//         }
//}
//   }
   
   }