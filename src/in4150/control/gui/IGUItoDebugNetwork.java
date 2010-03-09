package in4150.control.gui;

import in4150.control.BufferedMessage;

import java.util.List;
import java.util.Observable;

public abstract class IGUItoDebugNetwork extends Observable
{
	public abstract List<BufferedMessage> getMessageBuffer();

	public abstract void deliverMessage(BufferedMessage pMessage);
}