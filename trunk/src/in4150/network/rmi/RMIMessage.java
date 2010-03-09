package in4150.network.rmi;


import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
/**
 * message container with all RMI specific stuff
 * content will be unpacked by other end of the RMI connection
 * 
 * TODO: might need an id and/or senttime?
 * 
 * @author Peter Dijkshoorn
 *
 */
public class RMIMessage implements Serializable, Runnable{
	/**
	 * to be able to correctly serialize this class
	 */
	private static final long serialVersionUID = 2066773020858558356L;
	private int delay;
	private RMIAddress to;
	private RMIAddress from;
	private Object content;
	private boolean traveling;
	
	
	public RMIMessage(RMIAddress from, RMIAddress to, Object content){
		this(from,to,content,5000);
	}
	public RMIMessage(RMIAddress from, RMIAddress to, Object content, int delay){
		this.from = from;
		this.to = to;
		this.content = content;
		this.delay = delay;
		this.traveling = false;
	}
	
	public Object getContent(){
		return this.content;
	}
	
	public boolean isTraveling(){
		return this.traveling;
	}
	
	/**
	 * starts the travel of the message along the dark tubes of cyberspace
	 * this way artificial delay for debug and demonstration purposes is implemented
	 */
	public void travel(){
		new Thread(this).start();
	}
	
	/**
	 * the thread sleeps {@code this.delay} amount of time after which it arrives at the destination
	 */
	public void run(){
		this.traveling = true;
		
		try {
			Thread.sleep(this.delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.traveling = false;
		try{
			/*
			// Lookup the network,
			ICOSocket lReceiver = (ICOSocket) Naming.lookup(CONetwork.NETWORK_URL);
			 */
			((IRMISocket)LocateRegistry.getRegistry(this.to.host,this.to.port).lookup(this.to.objectname)).receive(this.from, this);
		} catch (AccessException e) {
			// TODO something useful with the exception, failed to contact remote obj
			e.printStackTrace();
			System.exit(0);
		} catch (RemoteException e) {
			// TODO something useful with the exception, failed to contact remote obj
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			// TODO something useful with the exception, address does not exist
			e.printStackTrace();
			System.exit(0);
		} catch (NullPointerException e){
			// TODO something useful with the exception, address is empty
			e.printStackTrace();
			System.exit(0);
		}
		
	}
}
