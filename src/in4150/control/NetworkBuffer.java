package in4150.control;

import in4150.control.gui.IGUItoDebugNetwork;
import in4150.network.rmi.IRMIClient;
import in4150.network.rmi.RMIAddress;
import in4150.network.rmi.RMISocket;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class NetworkBuffer extends IGUItoDebugNetwork implements IRMIClient
{
	private final List<BufferedMessage> fMessages;

	private RMISocket fSocket;

	public NetworkBuffer()
	{
		fMessages = new ArrayList<BufferedMessage>();

		try
		{
			fSocket	= new RMISocket(Integer.toString(0), this);
		}
		catch (RemoteException lException)
		{
			lException.printStackTrace();
			System.exit(0);
		}
	}

	public void deliverMessage(int pIndex)
	{
		BufferedMessage lMessage = fMessages.remove(pIndex);

		fSocket.send(new RMIAddress(Integer.toString(lMessage.getReceiver())), lMessage.getMessage(), 0);

		this.setChanged();
		this.notifyObservers(fMessages.toArray());
	}

	private void bufferMessage(BufferedMessage pMessage)
	{
		fMessages.add(pMessage);

		this.setChanged();
		this.notifyObservers(fMessages.toArray());
	}

	@Override
	public void receive(Object pContent)
	{
		if (pContent instanceof BufferedMessage)
		{
			BufferedMessage lMessage = (BufferedMessage)pContent;

			this.bufferMessage(lMessage);
		}
	}

	@Override
	public void deliverMessage(BufferedMessage pMessage)
	{
		if (fMessages.contains(pMessage))
		{
			this.deliverMessage(fMessages.indexOf(pMessage));
		}
	}

	@Override
	public List<BufferedMessage> getMessageBuffer()
	{
		return fMessages;
	}
}