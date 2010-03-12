package in4150.control.gui.network;

import in4150.control.BufferedMessage;
import in4150.control.gui.IGUItoDebugNetwork;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

public class NetworkPanel extends JPanel implements ActionListener, Observer
{
	// Unique Serial ID.
	private static final long serialVersionUID = 8196794846314120471L;

	// All possible (Button press) Events.
	private static final String TRANSMIT_EVENT = "Transmit Message";

	// The Network being observed and controlled by this Frame.
	private final IGUItoDebugNetwork fMonitoredNetwork;

	// Selection List Object showing messages in transit.
	private final JList fMessageList;

	// Button to force sending the selected message.
	private final JButton fTransmitMessage;

	// Which message was selected when it was sent.
	private int fSelected;

	public NetworkPanel(IGUItoDebugNetwork pMonitoredNetwork)
	{
		// Initialize the frame with a title.
		super();

		// Store the Network locally.
		fMonitoredNetwork = pMonitoredNetwork;

		// Create the interface elements.
		fMessageList	 = new JList();
		fTransmitMessage = new JButton(TRANSMIT_EVENT);

		fSelected = 0;

		// Initialize the interface elements.
		this.initialize();
	}

	/**
	 * Delegated function for (re)setting the contents of the frame and
	 * its elements.
	 */
	private void initialize()
	{
		// Add this frame as a listener to the button events.
		fTransmitMessage.addActionListener(this);

		// Force the user to only select one message at a time.
		fMessageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Set the Layout of this frame, and add the elements.
		this.setLayout(new BorderLayout());
		this.add(fMessageList, BorderLayout.CENTER);
		this.add(fTransmitMessage, BorderLayout.SOUTH);

		// The frame is now ready to observe changes in the Network.
		fMonitoredNetwork.addObserver(this);
	}

	/**
	 * Function inherited from ActionListener. It is called whenever an Action has
	 * occurred (i.e. a Button was pressed).
	 * 
	 * @param pEvent - Contains the name of the Button that was pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent pEvent)
	{
		// If this was a call to transmit a message,
		if (pEvent.getActionCommand().equals(TRANSMIT_EVENT))
		{
			// Retrieve the selected message.
			BufferedMessage lMessage = (BufferedMessage)fMessageList.getSelectedValue();

			// Attempt to extract the selected index.
			fSelected = fMessageList.getSelectedIndex();

			if (lMessage != null)
			{
				// And force the Network to deliver it.
				fMonitoredNetwork.deliverMessage(lMessage);
			}
		}
	}

	/**
	 * Function inherited from Observer. It is called whenever the observed Network has
	 * changed.
	 * 
	 * Parameters are not used.
	 */
	@Override
	public void update(Observable pNetwork, Object pObject)
	{
		// Ask the Network for the current list of messages,
		fMessageList.setListData((Object[])pObject);

		// and update the display.
		this.repaint();

		fMessageList.setSelectedIndex(Math.min(fSelected, ((Object[])pObject).length-1));
	}
}
