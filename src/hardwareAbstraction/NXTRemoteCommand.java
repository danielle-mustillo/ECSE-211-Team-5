package hardwareAbstraction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import utilities.Communicator;

public class NXTRemoteCommand {
	   DataInputStream dis;
	   DataOutputStream dos;
	   
	   
	    public NXTRemoteCommand(Communicator communicator) {
	    	this.dis = communicator.dis;
	    	this.dos = communicator.dos;
	    }
	   
	    public void send(int id,int command)
	     {
	       try
	       { dos.writeInt(id);
	         dos.writeInt(command);
	        
	         dos.flush();
	       
	         
	       } catch (IOException e)
	       {
	       
	         System.out.println("send problem " + e);
	          
	         }
	       }
	    public void send(int id,int command, int param1 ,boolean immediateReturn)
	     {
	       try
	       { dos.writeInt(id);
	         dos.writeInt(command);
	         dos.writeInt(param1);
	         dos.writeBoolean(immediateReturn);
	         dos.flush();
	        
	         
	       } catch (IOException e)
	       {
	       
	         System.out.println("send problem " + e);
	          
	         }
	       }
	    
	    public void send(int id,int command, int param1 )
	     {
	       try
	       { dos.writeInt(id);
	         dos.writeInt(command);
	         dos.writeInt(param1);
	        
	         dos.flush();
	         
	         
	       } catch (IOException e)
	       {
	       
	         System.out.println("send problem " + e);
	          
	         }
	       }
	    public int getInt() throws IOException{
	       
	       return dis.readInt();
	       
	    }
	 public float getFloat() throws IOException{
	       
	       return dis.readFloat();
	       
	    }
	 
	 public boolean getBool() throws IOException{
	    
	    return dis.readBoolean();
	    
	 }
}
