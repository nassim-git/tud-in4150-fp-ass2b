

package in4150.network.rmi;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;



/**
 * RMI end point
 * receives RMI messages from RMI calls, unpacks them and sends them upward in the stack
 * @author Peter Dijkshoorn
 *
 */
public class RMISocket extends UnicastRemoteObject implements IRMISocket{
	/**
	 * serial needed to avoid serializable problems
	 */
	private static final long serialVersionUID = 6776813661285758657L;
	private RMIAddress address;
	private IRMIClient client;
	
	/**
	 * installs a RMI Socket by stating an address and registers it
	 * 
	 * @param address
	 * @param client
	 * @throws RemoteException
	 */
	public RMISocket(String address, IRMIClient client) throws RemoteException{
		this.address = new RMIAddress(address);
		this.client = client;

		this.register();
	}
	
	/**
	 * sends an object to the socket located at the address specified
	 * with a hardcoded default delay
	 * 
	 * @param to
	 * @param load
	 * 
	 * @author Peter Dijkshoorn
	 */
	public void send(RMIAddress to, Object load){
		this.send(to,load,5000);
	}
	/**
	 * sends an object to the socket located at the address specified
	 * 
	 * @param to
	 * @param load
	 * @param delay time travelling will take
	 * 
	 * @author Peter Dijkshoorn
	 */
	public void send(RMIAddress to,Object load, int delay){
		RMIMessage message = new RMIMessage(this.address,to,load, delay);
		message.travel();
	}

	/**
	 * receives a message from the socket on the other end
	 * passes it to the upper layer
	 * 
	 * @param msg 
	 * @throws RemoteException
	 * 
	 * @author Peter Dijkshoorn
	 */
	public void receive(RMIAddress from, RMIMessage msg) throws RemoteException{
		client.receive(msg.getContent());
	}
	
	/**
	 * starts RMI register if not present at port stated in our address
	 * starts security manager
	 * registers this socket
	 * 
	 * @author Peter Dijkshoorn
	 */
	public void register(){
		// start RMI register
		try {
			java.rmi.registry.LocateRegistry.createRegistry(this.address.port);
		} catch(ExportException e){
			// Register is already started
		} catch(RemoteException e){
			// TODO something useful with the exception, failed to create register
			e.printStackTrace();
			System.exit(0);
		}
		
		// check if security manager is present, else create it.
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		
/*
		// Create the Process stub.
		ICOSocket lStub = (ICOSocket) UnicastRemoteObject.exportObject(this, 0);

		// Register the stub.
		Naming.rebind(lURL, lStub);
*/
		
		// register with the rmi register, rebind() instead of bind() to override previous binds
		try {
			LocateRegistry.getRegistry().rebind(this.address.objectname, this);
		} catch (AccessException e) {
			// TODO something useful with the exception
			e.printStackTrace();
			System.exit(0);
		} catch (RemoteException e) {
			// TODO something useful with the exception
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * unregisters this socket
	 * 
	 * @author Peter Dijkshoorn
	 */
	public void unregister(){
		try {
			LocateRegistry.getRegistry(this.address.port).unbind(this.address.objectname);
		} catch (AccessException e) {
			// TODO something useful with the exception
			e.printStackTrace();
			System.exit(0);
		} catch (RemoteException e) {
			// TODO something useful with the exception
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			// TODO something useful with the exception
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * This function returns the names of all RMISockets currently registered
	 * at the registry located at the provided port.
	 * 
	 * @param pPort - Location of the registry being searched.
	 * 
	 * @return A list of names of registered RMISockets.
	 */
	public static String[] getConnectedSockets(int pPort)
	{
		// List of names, starts out empty.
		ArrayList<String> lRegistered = new ArrayList<String>();

		// The registry being searched.
		Registry lRegistry = null;

		// Attempt to locate the registry.
		try
		{
			lRegistry = LocateRegistry.getRegistry(pPort);
		}
		catch (Exception e)
		{
		}

		// If the registry was found,
		if (lRegistry != null)
		{
			String[] lList = null;

			// Attempt to read the registry's list of registered elements.
			try
			{
				lList = lRegistry.list();
			}
			catch (Exception e)
			{
			}

			if (lList != null)
			{
				// Go over the list looking for Sockets.
				for (String lDiscovered : lList)
				{
					try
					{
						if (lRegistry.lookup(lDiscovered) instanceof IRMISocket)
						{
							lRegistered.add(lDiscovered);
						}
					}
					catch (Exception e)
					{
					}
				}
			}
		}

		// Return the list of discovered Sockets.
		return lRegistered.toArray(new String[lRegistered.size()]);
	}
}