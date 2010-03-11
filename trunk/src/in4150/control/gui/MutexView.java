package in4150.control.gui;

import in4150.control.gui.network.NetworkPanel;
import in4150.control.gui.process.ProcessPanel;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * The MutexView implements the overall Frame used to display the operation of the
 * mutual exclusion processes and underlying network.
 * 
 * Can be used to perform manual use cases.
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class MutexView extends JFrame
{
	// Unique Serial ID.
	private static final long serialVersionUID = -3820402804045322410L;

	private ArrayList<ProcessPanel> fProcesses;

	/**
	 * Constructs a single frame containing two panels, one for all the processes,
	 * and one for the network.
	 * 
	 * @param pProcesses - All processes to display and control.
	 * @param pNetwork - The network to display and control.
	 */
	public MutexView(IGUItoApplication[] pProcesses, IGUItoDebugNetwork pNetwork)
	{
		// Set initial position and (fixed) size.
		this.setLocation(50, 50);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the layout to a single row.
		this.setLayout(new GridLayout(1,0));

		// If we have processes to display,
		if (pProcesses != null)
		{
			fProcesses = new ArrayList<ProcessPanel>();
			JPanel lProcessGroup = new JPanel();
			lProcessGroup.setLayout(new GridLayout(0, 1));

			// Add a number of ProcessPanels
			for (IGUItoApplication lProcess : pProcesses)
			{
				if (lProcess != null)
				{
					ProcessPanel lPanel = new ProcessPanel(lProcess);
					fProcesses.add(lPanel);
					lProcessGroup.add(lPanel);
				}
			}

			this.add(lProcessGroup);
		}

		// If we have a network to display,
		if (pNetwork != null)
		{
			// Add a NetworkPanel.
			this.add(new NetworkPanel(pNetwork));
		}

		// If we have processes to display,
		if (pProcesses != null)
		{
			// Start the update task.
			DataUpdateTask lUpdateTask = new DataUpdateTask();
			lUpdateTask.execute();
		}
	}

	/**
	 * The DataUpdateTask is a background thread responsible
	 * for keeping the GUI up to date with the actual system state.
	 * 
	 * @author Frits de Nijs
	 */
	class DataUpdateTask extends SwingWorker<Void, Void>
	{
		/**
		 * Equivalent to run in meaning, this function simply handles the updates
		 * in an infinite loop, waiting some time every cycle.
		 */
		@Override
		protected Void doInBackground()
		{
			// If we ever decide to allow stopping only the GUI, this allows leaving the loop.
			try
			{
			while (!this.isCancelled())
			{
				for (ProcessPanel lPanel : fProcesses)
				{
					lPanel.updateNow();
				}

				// Make the update visible.
				if (isVisible()) repaint();

				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
			}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
	}
}