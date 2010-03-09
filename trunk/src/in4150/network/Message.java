package in4150.network;

import java.io.Serializable;

/**
 * A Message is a single entity that can be sent over the Network.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public abstract class Message implements Serializable
{
	// Unique Serial ID.
	private static final long serialVersionUID = 6143897767773747759L;
}