package in4150.mutex;

import in4150.network.Message;

/**
 * This interface defines the functions a Network should provide to the Mutual
 * Exclusion layer.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public interface IMutexToNetwork
{
	/**
	 * This function attempts to connect us to the Network. 
	 */
	public void connect();

	/**
	 * This function attempts to disconnect us from the Network. 
	 */
	public void disconnect();

	/**
	 * This function retrieves the ID we have been assigned by the Network.
	 * 
	 * Only available when connected.
	 * 
	 * @return Our own ID.
	 */
	public int getOwnID();

	/**
	 * This function retrieves the IDs of all connected elements.
	 * 
	 * Only available when connected.
	 * 
	 * @return All connected IDs (including our own).
	 */
	public Integer[] getConnectedIDs();

	/**
	 * This function allows a user of the network to transmit a single Message to
	 * one receiver.
	 * 
	 * @param pMessage  - The Message to deliver.
	 * @param pReceiver - The process that should receive the message.
	 */
	public void sendMessage(Message pMessage, int pReceiver);

	/**
	 * This function allows a user of the network to transmit a single Message to
	 * all receivers.
	 * 
	 * @param pMessage - The Message to deliver.d
	 */
	public void broadcastMessage(Message pMessage);
}