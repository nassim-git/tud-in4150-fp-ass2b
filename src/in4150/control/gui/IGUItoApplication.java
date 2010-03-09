package in4150.control.gui;

/**
 * This interface defines the functions an application with critical sections must 
 * provide to the monitoring user interface.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public interface IGUItoApplication
{
	/**
	 * This function asks the application if it is in its critical section right now.
	 * 
	 * @return true iff the application is in its critical section.
	 */
	public boolean inCriticalSection();

	/**
	 * This function asks the application if it wants to enter its critical section.
	 * 
	 * @return true iff the application has requested to enter its critical section.
	 */
	public boolean requestedCriticalSection();

	/**
	 * This function tells the application to try to enter its critical section.
	 */
	public void requestCriticalSection();

	/**
	 * This function asks the application for its mutual exclusion controller.
	 * 
	 * @return A user interface port to the mutual exclusion controller used by this application.
	 */
	public IGUItoMutex getMutexController();
}