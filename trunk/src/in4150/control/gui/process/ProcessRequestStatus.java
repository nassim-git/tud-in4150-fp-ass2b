package in4150.control.gui.process;

import in4150.control.gui.IGUItoMutex;
import in4150.mutex.VectorClock;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ProcessRequestStatus extends JPanel
{
	// Unique Serial ID.
	private static final long serialVersionUID = -4880716258131156635L;

	private final IGUItoMutex fMutexController;

	private final JLabel fClockLabel;

	public ProcessRequestStatus(IGUItoMutex pMutexController)
	{
		fMutexController = pMutexController;

		fClockLabel = new JLabel("", JLabel.CENTER);

		this.add(fClockLabel);
	}

	public void updateNow()
	{
		VectorClock lRequests = fMutexController.getRequests();

		fClockLabel.setText(lRequests.toString());
	}
}