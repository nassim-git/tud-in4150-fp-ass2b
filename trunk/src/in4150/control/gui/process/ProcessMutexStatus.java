package in4150.control.gui.process;

import in4150.control.gui.IGUItoApplication;
import in4150.control.gui.IGUItoMutex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class ProcessMutexStatus extends JPanel
{
	// Unique Serial ID.
	private static final long serialVersionUID = -5757087529854196825L;

	private final Label fRequestIndicator;
	private final Label fTokenIndicator;
	private final Label fCriticalIndicator;

	private final IGUItoApplication fApplication;
	private final IGUItoMutex fMutex;

	public ProcessMutexStatus(IGUItoApplication pApplication, IGUItoMutex pMutex)
	{
		super();

		fApplication = pApplication;
		fMutex		 = pMutex;

		fRequestIndicator	= new Label("REQ", Label.CENTER);
		fTokenIndicator		= new Label("TOK", Label.CENTER);
		fCriticalIndicator	= new Label("CS", Label.CENTER);

		GridLayout lLayout = new GridLayout(1,3);
		lLayout.setHgap(5);
		lLayout.setVgap(5);
		this.setLayout(lLayout);

		this.addPanel(fRequestIndicator);
		this.addPanel(fTokenIndicator);
		this.addPanel(fCriticalIndicator);
	}

	private void addPanel(Label lLabel)
	{
		JPanel lPanel = new JPanel();

		lPanel.setLayout(new BorderLayout());
		lPanel.add(lLabel);
		lPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		this.add(lPanel);
	}

	public void updateNow()
	{
		if (fApplication.requestedCriticalSection())
		{
			fRequestIndicator.setBackground(new Color(220, 220, 0));
		}
		else
		{
			fRequestIndicator.setBackground(null);
		}

		if (fMutex.hasToken())
		{
			fTokenIndicator.setBackground(new Color(0, 220, 0));
		}
		else
		{
			fTokenIndicator.setBackground(null);
		}

		if (fApplication.inCriticalSection())
		{
			fCriticalIndicator.setBackground(new Color(220, 0, 0));
		}
		else
		{
			fCriticalIndicator.setBackground(null);
		}
	}
}