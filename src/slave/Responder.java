package slave;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.LCPResponder;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.RS485;

/**
 * The responder class responds to the LCP requests sent by the Communicator.java class. 
 * Uses RS-485.
 * 
 * 
 * <p> 
 * 
 * @author danielle
 * @author Riley
 * 
 */
public class Responder {
	
//	/**
//	 * The subclass that handles the LCP connections. In particular, it is modified version of the LCPResponder to shutdown the connection once the program disconnects.
//	 * @author Andy Shaw
//	 * @author danielle
//	 *
//	 */
//	public static class ResponderTool extends LCPResponder {
//		ResponderTool(NXTCommConnector con) {
//            super(con);
//        }

 //        protected void disconnect() {
 //           super.disconnect();
 //           super.shutdown();
 //       }
//	}
	
	/**
	 * This program has a main as it is to be loaded on the slaveNXT brick. 
	 * <p>
	 * It will start the {@link RemoteUltrasonicPoller} and the {@link NXTRemoteControl}
	 * @param args
	 *            This is the default constructor, not needed.
	 */
	
	public static void main(String[] args) throws Exception {
		
//		RConsole.openUSB(20000);
		LCD.drawString("Connecting", 1, 1);
//		ResponderTool resp = new ResponderTool(RS485.getConnector());
//		resp.start();
//		resp.join();
		RemoteUltrasonicPoller remoteUSP = new RemoteUltrasonicPoller();
		NXTRemoteControl motorControl = new NXTRemoteControl(MotorPort.A, MotorPort.B, MotorPort.C, remoteUSP);
		
		motorControl.start();
		
    }
	
}