package in4150.mutex;

import in4150.network.Message;

/**
 * A Request message asks the process which receives it for permission to
 * enter the critical section.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class Request extends Message
{
	// Unique Serial ID.
	private static final long serialVersionUID = 893125914590790467L;

	// The ID of the process doing the request.
	private final int fRequester;

	// The sequence number of this request.
	private final int fRequestNumber;

	/**
	 * Constructs a new Request.
	 * 
	 * @param pRequester - The ID of the process doing the request.
	 * @param pRequestNumber - The sequence number of this request.
	 */
	public Request(int pRequester, int pRequestNumber)
	{
		fRequester		= pRequester;
		fRequestNumber	= pRequestNumber;
	}

	/**
	 * @return The ID of the process doing the request.
	 */
	public int getRequester()
	{
		return fRequester;
	}

	/**
	 * @return The sequence number of this request.
	 */
	public int getRequestNumber()
	{
		return fRequestNumber;
	}

	@Override
	public String toString()
	{
		String lResult = "Request number " + fRequestNumber + " from Process " + fRequester;

		return lResult;
	}
}