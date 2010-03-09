package in4150.mutex;

/**
 * This interface defines the functions an Application wanting to use Mutual
 * Exclusion must provide to the Mutual Exclusion layer.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public interface IMutexToApplication
{
	/**
	 * This function tells the application that it must now execute its Critical
	 * Section.
	 */
	public void doCriticalSection();

	/**
	 * This function asks the application if it is in its critical section right now.
	 * 
	 * @return true iff the application is in its critical section.
	 */
	public boolean inCriticalSection();
}