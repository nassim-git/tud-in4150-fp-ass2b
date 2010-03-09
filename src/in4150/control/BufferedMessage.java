package in4150.control;

import in4150.network.Message;

public class BufferedMessage extends Message
{
	//
	private static final long serialVersionUID = 3786635339497071400L;

	private final Message fMessage;
	private final int fReceiver;

	public BufferedMessage(Message pMessage, int pReceiver)
	{
		fMessage	= pMessage;
		fReceiver	= pReceiver;
	}

	public Message getMessage()
	{
		return fMessage;
	}

	public int getReceiver()
	{
		return fReceiver;
	}

	@Override
	public String toString()
	{
		String lResult = fMessage.toString() + " to Process " + fReceiver;

		return lResult;
	}
}