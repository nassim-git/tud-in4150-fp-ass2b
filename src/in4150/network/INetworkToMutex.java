package in4150.network;

public interface INetworkToMutex
{
	public void processConnected(int pProcessID);

	public void processDisconnected(int pProcessID);

	public void receiveMessage(Message pMessage);
}