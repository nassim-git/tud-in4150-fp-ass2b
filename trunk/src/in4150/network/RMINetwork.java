package in4150.network;

import in4150.mutex.IMutexToNetwork;
import in4150.network.rmi.IRMIClient;
import in4150.network.rmi.RMIAddress;
import in4150.network.rmi.RMISocket;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The RMINetwork is one type of Network that can be used to transmit messages from
 * one point to another. It uses the Java RMI system to communicate.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class RMINetwork implements IRMIClient, IMutexToNetwork
{
	// The layer above us.
	private final INetworkToMutex fMutexLayer;

	// The layer below us.
	private RMISocket fSocket;

	// True iff our socket is connected.
	private boolean fConnected;

	// Our personal location in the network ring.
	private int fID;

	// All known elements in the network ring.
	private final ArrayList<Integer> fConnectedIDs;

	/**
	 * Constructs a new RMINetwork for the provided layer.
	 * 
	 * @param pMutexLayer - The layer that should receive incoming messages.
	 */
	public RMINetwork(INetworkToMutex pMutexLayer)
	{
		fMutexLayer		= pMutexLayer;
		fConnected		= false;
		fSocket			= null;
		fID				= 0;
		fConnectedIDs	= new ArrayList<Integer>();
	}

	/**
	 * Connects this RMINetwork to any other networks currently active.
	 */
	@Override
	public void connect()
	{
		// Connecting only makes sense when we are not connected now.
		if (!fConnected)
		{
			// First, look for processes that are already active.
			this.discoverConnectedSockets();
	
			// Then, search for a free ID.
			fID = 0;
			while (!fConnected)
			{
				fID++;
				if (!fConnectedIDs.contains(fID))
				{
					try
					{
						// This ones free, connect.
						fSocket = new RMISocket(Integer.toString(fID), this);
						fConnectedIDs.add(fID);
						fConnected = true;
					}
					catch (RemoteException lException)
					{
						lException.printStackTrace();
						System.exit(0);
					}
				}
			}

			// Notify any active processes, we are here as well.
			this.notifyConnected();
		}
	}

	/**
	 * Searches the RMI registry for active networks, and populates
	 * the ConnectedIDs array with their IDs.
	 */
	private void discoverConnectedSockets()
	{
		// Look for connected RMI Sockets.
		String[] lConnected = RMISocket.getConnectedSockets(Registry.REGISTRY_PORT);

		// Clear our current knowledge.
		fConnectedIDs.clear();

		// Look for Sockets with a number.
		for (String lOther : lConnected)
		{
			try
			{
				Integer lOtherID = Integer.decode(lOther);

				if (lOtherID != 0)
				{
					fConnectedIDs.add(lOtherID);
				}
			}
			catch (NumberFormatException lException)
			{
			}
		}
	}

	/**
	 * Informs all other connected networks that we are now connected as well.
	 */
	private void notifyConnected()
	{
		// The notification to send.
		Notification lNotification = new Notification(NotificationType.CONNECTED, fID);

		// Send it to all others.
		for (int lID : fConnectedIDs)
		{
			if (lID != fID)
			{
				fSocket.send(new RMIAddress(Integer.toString(lID)), lNotification, 0);
			}
		}
	}

	/**
	 * Disconnects this Network from the others and the rmi registry.
	 */
	@Override
	public void disconnect()
	{
		// Disconnect only makes sense if we are connected.
		if (fConnected)
		{
			// Inform the others.
			this.notifyDisconnected();

			// And close the socket.
			fSocket.unregister();
			fSocket = null;

			fConnected = false;
		}
	}

	/**
	 * Informs all other connected networks that we are no longer connected.
	 */
	private void notifyDisconnected()
	{
		// The notification to send.
		Notification lNotification = new Notification(NotificationType.DISCONNECTED, fID);

		// Send it to all others.
		for (int lID : fConnectedIDs)
		{
			if (lID != fID)
			{
				fSocket.send(new RMIAddress(Integer.toString(lID)), lNotification, 0);
			}
		}
	}

	/**
	 * Called when the Socket receives a message.
	 * 
	 * @param pContent contains the information that was sent.
	 */
	@Override
	public void receive(Object pContent)
	{
		// If this was a network message,
		if (pContent instanceof Message)
		{
			// Pass it along.
			fMutexLayer.receiveMessage((Message)pContent);
		}
		// If this was a network notification,
		else if (pContent instanceof Notification)
		{
			Notification lNotification = (Notification)pContent;

			// Attempt to interpret it.
			switch (lNotification.getNotification())
			{
				case CONNECTED:
				{
					if (!fConnectedIDs.contains(lNotification.getNotifyingProcess()))
					{
						// This is a new connecting ID, add it.
						fConnectedIDs.add(new Integer(lNotification.getNotifyingProcess()));
						fMutexLayer.processConnected(lNotification.getNotifyingProcess());
					}
					break;
				}
				case DISCONNECTED:
				{
					if (fConnectedIDs.contains(lNotification.getNotifyingProcess()))
					{
						// This was a connected ID, remove it.
						fConnectedIDs.remove(new Integer(lNotification.getNotifyingProcess()));
						fMutexLayer.processDisconnected(lNotification.getNotifyingProcess());
					}
					break;
				}
			}
		}
	}

	/**
	 * broadcastMessage sends the provided message to ALL others.
	 */
	@Override
	public void broadcastMessage(Message pMessage)
	{
		if (fConnected)
		{
			for (int lID : fConnectedIDs)
			{
				this.sendMessage(pMessage, lID);
			}
		}
	}

	/**
	 * sendMessage sends the provided message to the intended receiver.
	 */
	@Override
	public void sendMessage(Message pMessage, int pReceiver)
	{
		if (fConnected)
		{
			fSocket.send(new RMIAddress(Integer.toString(pReceiver)), pMessage, 0);
		}
	}

	@Override
	public Integer[] getConnectedIDs()
	{
		Integer[] lResult = new Integer[fConnectedIDs.size()];
		lResult = fConnectedIDs.toArray(lResult);
		Arrays.sort(lResult);
		return lResult;
	}

	@Override
	public int getOwnID()
	{
		return fID;
	}
}