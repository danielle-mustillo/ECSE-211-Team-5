package utilities;

import lejos.nxt.comm.LCPResponder;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.RS485;

/**
 * The responder class responds to the LCP requests sent by the Communicator.java class. 
 * Uses by default RS485 without exception for a reliable, stable connection.
 * <p> 
 * @author danielle
 * 
 */
public class Responder {
	/**
	 * The subclass that handles the LCP connections. In particular, it is modified version of the LCPResponder to shutdown the connection once the program disconnects.
	 * @author danielle
	 *
	 */
	public static class ResponderTool extends LCPResponder {
		ResponderTool(NXTCommConnector con) {
            super(con);
        }

         protected void disconnect() {
            super.disconnect();
            super.shutdown();
        }
	}
	
	/**
	 * This program has a main as it is to be loaded on the slaveNXT brick. Has
	 * almost no functionality otherwise.
	 * <p>
	 * @param args
	 *            This is the default constructor, not needed.
	 */
	
	public static void main(String[] args) {
		ResponderTool resp = new ResponderTool(RS485.getConnector());
		resp.start();
		try {
			resp.join();
		} catch (InterruptedException e) {
			Communicator.catchBug("The responder failed in the Responder.java class " + e.getMessage());
		}
    }
	
}