package hardwareAbstraction;

import java.io.IOException;

import lejos.nxt.comm.RConsole;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class NXTRemoteMotor implements RemoteCommands, RegulatedMotor{
	   
	   

    private int id;

    private NXTRemoteCommand  motorCommand;
  
  //  private ListenerConnection listenerConnection=null;

    public  NXTRemoteMotor(NXTRemoteCommand nxtCommand, int id){
       
        this.id = id;
        this.motorCommand=nxtCommand;
         //this.listenerConnection=listenersConnection;
    }
    
 
    public boolean suspendRegulation(){
     boolean suspended=false;
       motorCommand.send(id, SUSPEND_REGULATION);
        try {
           suspended=motorCommand.getBool();
           
        } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
        }
       
    return suspended;
    }
@Override
public void flt() {
  RConsole.println("flt");
  motorCommand.send(id, FLT);
  
}

@Override
public int getRotationSpeed() {
  int speed=0;
  RConsole.println("getRotationSpeed");
  motorCommand.send(id, GET_ROTATION_SPEED);
  try {
     speed=motorCommand.getInt();
     RConsole.println("speed="+speed);
  } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
  }
  return speed;
}

@Override
public int getTachoCount() {
  RConsole.println("getTachoCount");
  int tachoCount=0;
  motorCommand.send(id, GET_TACHO_COUNT);
  try {
     tachoCount=motorCommand.getInt();
     RConsole.println("tachoCount="+tachoCount);
  } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
  }
  return tachoCount;
}

@Override
public void resetTachoCount() {
  RConsole.println("resetTachoCount");
  motorCommand.send(id, RESET_TACHO_COUNT);
  
}

@Override
public int getLimitAngle() {
  RConsole.println("getLimitAngle");
  
  int angle=0;
  motorCommand.send(id, GET_LIMIT_ANGLE);
  try {
     angle=motorCommand.getInt();
     RConsole.println("angle="+angle);
  } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
  }
  return angle;
}

@Override
public float getMaxSpeed() {
  RConsole.println("getMaxSpeed=");
  float speed=0;
  motorCommand.send(id,GET_MAX_SPEED);
  try {
     speed=motorCommand.getFloat();
     RConsole.println("speed="+speed);
  } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
  }
  return speed;
}

@Override
public int getSpeed() {
  RConsole.println("getSpeed=");
  int speed=0;
  motorCommand.send(id, GET_SPEED);
  try {
     speed=motorCommand.getInt();
     RConsole.println("speed="+speed);
  } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
  }
  return speed;
}

@Override
public boolean isStalled() {
  RConsole.println("isStalled");
  boolean isStalled=false;
  motorCommand.send(id, IS_STALLED);
  try {
     isStalled=motorCommand.getBool();
     RConsole.println("isStalled="+isStalled);
  } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
  }
  return isStalled;

}

@Override
public void rotate(int angle) {
  RConsole.println("rotate");
  motorCommand.send(id, ROTATE, angle, false);
  
}

@Override
public void rotate(int angle, boolean immediateReturn) {
  RConsole.println("rotate");
  motorCommand.send(id, ROTATE, angle, immediateReturn);
  
}

@Override
public void rotateTo(int angle) {
  RConsole.println("rotateTo");
  motorCommand.send(id, ROTATE_TO, angle, false);
}

@Override
public void rotateTo(int angle, boolean immediateReturn) {
  motorCommand.send(id, ROTATE_TO, angle, immediateReturn);
}

@Override
public void setAcceleration(int accel) {

  motorCommand.send(id, SET_ACCELERATION, accel);
  
}

@Override
public void setSpeed(int speed) {
  RConsole.println("setSpeed " +speed);
  motorCommand.send(id, SET_SPEED, speed);
  
}

@Override
public void backward() {
  motorCommand.send(id, BACKWARD);
  
}

@Override
public void forward() {
  
  motorCommand.send(id, FORWARD);
  
}

@Override
public boolean isMoving() {
  RConsole.println("isMoving=");
  
  boolean isMoving=false;
  motorCommand.send(id, IS_MOVING);
  try {
     isMoving=motorCommand.getBool();
     RConsole.println("isMoving="+isMoving);
  } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
  }
  return isMoving;
}

@Override
public void stop() {
  motorCommand.send(id, STOP);
  
}


@Override
public void addListener(RegulatedMotorListener arg0) {
	// TODO Auto-generated method stub
	
}


@Override
public void flt(boolean arg0) {
	// TODO Auto-generated method stub
	
}


@Override
public void stop(boolean arg0) {
	// TODO Auto-generated method stub
	
}


@Override
public void waitComplete() {
	// TODO Auto-generated method stub
	
}

}
