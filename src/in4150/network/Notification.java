package in4150.network;

import java.io.Serializable;

/**
 * A Notification is used to inform others in the network about network events.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class Notification implements Serializable
{
	// Unique Serial ID
	private static final long serialVersionUID = -95495152847004654L;

	// The event this notification is about.
	private final NotificationType fNotification;

	// The process sending this notification.
	private final int fNotifyingProcess;

	/**
	 * Constructs a new Notification.
	 * 
	 * @param pType - The event this notification is about.
	 * @param pProcess - The process sending this notification.
	 */
	public Notification(NotificationType pType, int pProcess)
	{
		fNotification		= pType;
		fNotifyingProcess	= pProcess;
	}

	/**
	 * @return The event this notification is about.
	 */
	public NotificationType getNotification()
	{
		return fNotification;
	}

	/**
	 * @return The process sending this notification.
	 */
	public int getNotifyingProcess()
	{
		return fNotifyingProcess;
	}
}