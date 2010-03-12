package in4150.mutex;

import in4150.application.IApplicationToMutex;
import in4150.control.DebugRMINetwork;
import in4150.control.gui.IGUItoMutex;
import in4150.network.INetworkToMutex;
import in4150.network.Message;
import in4150.network.RMINetwork;

/**
 * The SuzukiKasamiMutex controller allows an application to coordinate access to its
 * critical section with a number of other processes.
 * 
 * The controller uses the Suzuki-Kasami token based mutual exclusion algorithm as 
 * described in the Lecture Notes, page 52.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class SuzukiKasamiMutex implements IGUItoMutex, IApplicationToMutex, INetworkToMutex
{
	// Interface to the application layer above us.
	private final IMutexToApplication fApplication;

	// Interface to the network layer below us.
	private final IMutexToNetwork fNetwork;

	// Vector maintaining the known requests as received from the other processes.
	private final VectorClock fCSRequests;

	// A container for the Token. If we don't have the Token, this is null.
	private Token fToken;

	// A flag indicating to the application that it may indeed enter its critical section now.
	private boolean fCanEnterCS;

	// A flag indicating to the application that it may indeed enter its critical section now.
	private boolean fProcessingToken;

	/**
	 * Constructs a new SuzukiKasamiMutex controller for the provided application.
	 * 
	 * @param pApplication - The application that wants to negotiate for access to Critical Section.
	 * @param pManualMode - Debug variable. If true, the controller uses the debug network.
	 */
	public SuzukiKasamiMutex(IMutexToApplication pApplication, boolean pManualMode)
	{
		fApplication	 = pApplication;
		fCSRequests		 = new VectorClock();
		fCanEnterCS		 = false;
		fProcessingToken = false;

		if (pManualMode)
		{
			fNetwork = new DebugRMINetwork(this);
		}
		else
		{
			fNetwork = new RMINetwork(this);
		}

		// Connect to the others.
		fNetwork.connect();

		// If we are the first, we get the Token.
		if (fNetwork.getConnectedIDs().length == 1)
		{
			fToken = new Token(new VectorClock());
		}

		// Store all other connected networks for easy access.
		Integer[] lConnected = fNetwork.getConnectedIDs();
		for (Integer lPeer : lConnected)
		{
			fCSRequests.put(Integer.toString(lPeer), 0);
		}
	}

	/**
	 * Function called by the parent application if it wants to enter
	 * the critical section.
	 * 
	 * Requests permission to enter the CS from all other Mutex controllers.
	 */
	@Override
	public void requestCriticalSection()
	{
		// Obtain our own ID.
		int lMyID		= fNetwork.getOwnID();

		// Increment our own Request sequence number.
		fCSRequests.incrementClock(Integer.toString(lMyID));

		// Get our incremented Request number.
		int lMyRequest	= fCSRequests.get(Integer.toString(lMyID));

		// Broadcast the request.
		fNetwork.broadcastMessage(new Request(lMyID, lMyRequest));
	}

	/**
	 * Function called when a new request has been received. If we have the Token,
	 * we can send it to the requester.
	 * 
	 * @param pRequest - The received Request details.
	 */
	private void receiveRequest(Request pRequest)
	{
		// Update our knowledge of requests.
		fCSRequests.put(Integer.toString(pRequest.getRequester()), pRequest.getRequestNumber());

		// If we have the token, we should pass it along to the requesting process.
		if (this.hasToken() && !fProcessingToken)
		{
			sendToken(pRequest.getRequester());
		}
	}

	/**
	 * Function called when another process sends us the Token.
	 * 
	 * @param pToken - The received Token.
	 */
	private void receiveToken(Token pToken)
	{
		fProcessingToken = true;

		// Allow the parent process access to the critical section.
		fCanEnterCS = true;

		// Store the token.
		fToken = pToken;

		fApplication.doCriticalSection();

		fCanEnterCS = false;

		// Update the Token to include knowledge of our satisfied request.
		fToken.satisfiedRequest(Integer.toString(fNetwork.getOwnID()), fCSRequests.get(Integer.toString(fNetwork.getOwnID())));

		// Attempt to pass the token along to processes with a higher ID.
		for (int lProcess : fNetwork.getConnectedIDs())
		{
			if (lProcess > fNetwork.getOwnID())
			{
				if (this.hasToken() &&
					fCSRequests.get(Integer.toString(lProcess)) > 
					fToken.getSatisfiedRequests().get(Integer.toString(lProcess)))
				{

					this.sendToken(lProcess);
				}
			}
		}

		// Attempt to pass the token along to processes with a lower ID.
		for (int lProcess : fNetwork.getConnectedIDs())
		{
			if (lProcess < fNetwork.getOwnID())
			{
				if (this.hasToken() &&
					fCSRequests.get(Integer.toString(lProcess)) > 
					fToken.getSatisfiedRequests().get(Integer.toString(lProcess)))
				{
					this.sendToken(lProcess);
				}
			}
		}

		// Done looking through received requests.
		fProcessingToken = false;
	}

	/**
	 * Internal function handling the sending of the Token.
	 * 
	 * @param lProcess - Process that should receive the Token.
	 */
	private void sendToken(int lProcess)
	{
		Token lToken = fToken;

		// We can only send the token if we have it, and are not using it at the moment.
		if (this.hasToken() && !this.fCanEnterCS && !fApplication.inCriticalSection())
		{
			// Record that we no longer have it.
			fToken = null;

			// And send it.
			fNetwork.sendMessage(lToken, lProcess);
		}
	}


	/**
	 * Function called by the parent application just before entering
	 * the critical section.
	 * 
	 * @return true iff we the application is allowed to enter its critical section.
	 */
	@Override
	public boolean canEnterCriticalSection()
	{
		return fCanEnterCS && this.hasToken();
	}

	@Override
	public IGUItoMutex getGUIinterface()
	{
		return this;
	}

	@Override
	public void processConnected(int pProcessID)
	{
		fCSRequests.put(Integer.toString(pProcessID), 0);
	}

	@Override
	public void processDisconnected(int pProcessID)
	{
		fCSRequests.remove(Integer.toString(pProcessID));
	}

	@Override
	public void receiveMessage(Message pMessage)
	{
		if (pMessage instanceof Request)
		{
			this.receiveRequest((Request)pMessage);
		}
		else if (pMessage instanceof Token)
		{
			this.receiveToken((Token)pMessage);
		}
	}

	@Override
	public int getProcessID()
	{
		return fNetwork.getOwnID();
	}

	@Override
	public boolean hasToken()
	{
		return fToken != null;
	}

	@Override
	public VectorClock getRequests()
	{
		return fCSRequests.clone();
	}
}