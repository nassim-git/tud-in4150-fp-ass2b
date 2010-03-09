package in4150.control;

import in4150.network.INetworkToMutex;
import in4150.network.Message;
import in4150.network.RMINetwork;

public class DebugRMINetwork extends RMINetwork
{
	public DebugRMINetwork(INetworkToMutex pMutexLayer)
	{
		super(pMutexLayer);
	}

	/**
	 * sendMessage sends the provided message to the intended receiver.
	 */
	@Override
	public void sendMessage(Message pMessage, int pReceiver)
	{
		super.sendMessage(new BufferedMessage(pMessage, pReceiver), 0);
	}
}