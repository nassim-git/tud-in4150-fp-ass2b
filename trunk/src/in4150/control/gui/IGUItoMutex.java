package in4150.control.gui;

import in4150.mutex.VectorClock;

public interface IGUItoMutex
{
	public int getProcessID();

	public boolean hasToken();

	public VectorClock getRequests();
}