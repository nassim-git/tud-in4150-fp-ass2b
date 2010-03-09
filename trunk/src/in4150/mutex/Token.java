package in4150.mutex;

import in4150.network.Message;

/**
 * The process which receives the message Token is allowed to enter its Critical Section 
 * once. The token remembers which requests have been satisfied.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class Token extends Message
{
	// Unique Serial ID.
	private static final long serialVersionUID = 6621256152983740001L;

	// This clock records which requests were satisfied.
	private final VectorClock fSatisfiedRequests;

	/**
	 * Constructs a new Token.
	 * 
	 * @param pSatisfiedRequests - The requests satisfied when this token was constructed.
	 */
	public Token(VectorClock pSatisfiedRequests)
	{
		fSatisfiedRequests = pSatisfiedRequests;
	}

	/**
	 * @return The requests that this Token has already satisfied.
	 */
	public VectorClock getSatisfiedRequests()
	{
		return fSatisfiedRequests;
	}

	/**
	 * Sets the Token satisfied requests to the provided value.
	 * 
	 * @param pProcess - Process that had its CS request satisfied.
	 * @param pRequest - The request that is satisfied.
	 */
	public void satisfiedRequest(String pProcess, int pRequest)
	{
		fSatisfiedRequests.put(pProcess, pRequest);
	}

	@Override
	public String toString()
	{
		String lResult = "Token " + fSatisfiedRequests.toString();

		return lResult;
	}
}