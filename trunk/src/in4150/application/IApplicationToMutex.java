package in4150.application;

import in4150.control.gui.IGUItoMutex;

/**
 * This interface defines the functions a Mutual Exclusion layer must provide
 * to the application layer.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public interface IApplicationToMutex
{
	/**
	 * This function requests permission to enter the critical section.
	 */
	public void requestCriticalSection();

	/**
	 * This function asks whether or not the application has permission to enter
	 * the critical section right now.
	 * 
	 * @return true iff this process can enter its critical section now.
	 */
	public boolean canEnterCriticalSection();

	/**
	 * This function requests the user interface monitor controls for this layer.
	 * 
	 * @return An interface to the gui functions of the mutex controller.
	 */
	public IGUItoMutex getGUIinterface();
}