package in4150.control.gui.process;

import in4150.control.gui.IGUItoApplication;
import in4150.control.gui.IGUItoMutex;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ProcessPanel extends JPanel implements ActionListener
{
	// Unique Serial ID.
	private static final long serialVersionUID = -709395356790449239L;

	private static final String REQUEST_EVENT = "REQ";

	private final IGUItoApplication fProcess;
	private final ProcessMutexStatus fMutexStatus;
	private final ProcessRequestStatus fRequestStatus;
	private final JButton fRequestButton;

	public ProcessPanel(IGUItoApplication pProcess)
	{
		super();

		fProcess		= pProcess;
		fMutexStatus	= new ProcessMutexStatus(fProcess, fProcess.getMutexController());
		fRequestStatus	= new ProcessRequestStatus(fProcess.getMutexController());
		fRequestButton	= new JButton(REQUEST_EVENT);

		fRequestButton.addActionListener(this);

		this.initialize();
	}

	private void initialize()
	{
		IGUItoMutex lMutexControl = fProcess.getMutexController();

		this.setLayout(new BorderLayout());

		JPanel lViewPanel = new JPanel();
		lViewPanel.setLayout(new GridLayout(1,2));

		JPanel lStatusPanel = new JPanel();
		lViewPanel.setLayout(new GridLayout(1,2));

		JLabel lProcessName = new JLabel("Process " + lMutexControl.getProcessID(), JLabel.LEFT);
		lProcessName.setPreferredSize(new Dimension(70, 15));
		lStatusPanel.add(lProcessName);
		lStatusPanel.add(fMutexStatus);

		lViewPanel.add(lStatusPanel);
		lViewPanel.add(fRequestStatus);

		this.add(lViewPanel, BorderLayout.CENTER);
		this.add(fRequestButton, BorderLayout.EAST);
	}

	@Override
	public void actionPerformed(ActionEvent pEvent)
	{
		// If this was a call to transmit a message,
		if (pEvent.getActionCommand().equals(REQUEST_EVENT))
		{
			if (!fProcess.requestedCriticalSection())
			{
				fProcess.requestCriticalSection();
			}
		}
	}

	public void updateNow()
	{
		fMutexStatus.updateNow();
		fRequestStatus.updateNow();

		if (fRequestButton != null && !fProcess.requestedCriticalSection())
		{
			fRequestButton.setEnabled(true);
		}
		else if (fRequestButton != null)
		{
			fRequestButton.setEnabled(false);
		}		
	}
}