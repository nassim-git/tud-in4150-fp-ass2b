package in4150.application;

import in4150.control.gui.IGUItoApplication;
import in4150.control.gui.IGUItoMutex;
import in4150.mutex.IMutexToApplication;
import in4150.mutex.SuzukiKasamiMutex;

/**
 * A MutexProcess is a Process that has shares a critical section with
 * other MutexProcesses. It uses a Mutual Exclusion controller to make sure 
 * only one MutexProcess enters its critical section at a time.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class MutexProcess implements IGUItoApplication, IMutexToApplication, Runnable
{
	// A debugging constant indicating how long a process must wait when actively running.
	private static final long RUNNING_SLEEP_TIME = 1000;

	// A debugging constant indicating how long a process must spend inside its critical section.
	private static final long CRITICAL_SECTION_TIME = 500;

	// A debugging constant indicating how likely it is for a Process to enter its critical section.
	private static final double CRITICAL_SECTION_CHANCE = 0.25;

	// The controller which guards access to the critical section.
	private final IApplicationToMutex fMutexController;

	// A flag indicating to ourself whether we have requested access to the critical section.
	private boolean fRequestedCriticalSection;

	// A flag indicating to ourself whether we are in the critical section.
	private boolean fInCriticalSection;

	// A flag indicating to ourself whether we are actively running.
	private boolean fRunning;

	/**
	 * Constructs a new MutexProcess. This function initializes the process and 
	 * its Mutual Exclusion controller.
	 */
	public MutexProcess(boolean pManualMode)
	{
		fMutexController			= new SuzukiKasamiMutex(this, pManualMode);
		fRequestedCriticalSection	= false;
		fInCriticalSection			= false;
		fRunning					= false;
	}

	/**
	 * Implements the processing functionality. A running MutexProcess will occasionally
	 * ask for permission to enter the critical section.
	 */
	@Override
	public void run()
	{
		// If this function was called, we are supposed to be running.
		fRunning = true;

		// While we are running...
		while (fRunning)
		{
			// Calculate a random sleep time.
			long lSleepTime = RUNNING_SLEEP_TIME + (long)(Math.random() * RUNNING_SLEEP_TIME);

			// Spend some time sleeping.
			try
			{
				Thread.sleep(lSleepTime);
			}
			catch (InterruptedException lException)
			{
				lException.printStackTrace();
				System.exit(0);
			}

			// Random chance of requesting the CS.
			double lRandomChance = Math.random();

			if (lRandomChance < CRITICAL_SECTION_CHANCE)
			{
				this.requestCriticalSection();
			}
		}
	}

	/**
	 * Function for handling requests to the critical section.
	 */
	@Override
	public void requestCriticalSection()
	{
		// We can only ask for permission if we haven't already.
		if (!fRequestedCriticalSection && !fInCriticalSection)
		{
			// Record our request.
			fRequestedCriticalSection = true;

			// Ask the controller for permission.
			fMutexController.requestCriticalSection();
		}
	}

	/**
	 * Implements the critical section of this process. Simply spends some time
	 * waiting in this example implementation.
	 */
	@Override
	public void doCriticalSection()
	{
		// Make sure we want to enter the CS, and that we actually can.
		if (fRequestedCriticalSection && fMutexController.canEnterCriticalSection())
		{
			// We are now in our CS.
			fInCriticalSection		  = true;

			// Calculate a random sleep time.
			long lSleepTime = CRITICAL_SECTION_TIME + (long)(Math.random() * CRITICAL_SECTION_TIME);

			// Spend some time sleeping.
			try
			{
				Thread.sleep(lSleepTime);
			}
			catch (InterruptedException lException)
			{
				lException.printStackTrace();
				System.exit(0);
			}

			// We have finished our CS, no longer requested.
			fRequestedCriticalSection = false;

			// We are now leaving our CS.
			fInCriticalSection		  = false;
		}
	}

	/**
	 * Returns the user interface access port to the mutual exclusion controller
	 * used by this process.
	 */
	@Override
	public IGUItoMutex getMutexController()
	{
		return fMutexController.getGUIinterface();
	}

	/**
	 * Returns true iff this process is in its critical section.
	 */
	@Override
	public boolean inCriticalSection()
	{
		return fInCriticalSection;
	}

	/**
	 * Returns true iff this process requested to enter its critical section.
	 */
	@Override
	public boolean requestedCriticalSection()
	{
		return fRequestedCriticalSection;
	}
}