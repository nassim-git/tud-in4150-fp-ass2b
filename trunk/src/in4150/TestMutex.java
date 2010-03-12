package in4150;

import in4150.application.MutexProcess;
import in4150.control.NetworkBuffer;
import in4150.control.gui.MutexView;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestMutex
{
	private static final int COMPONENTS = 5;
	private static final boolean MANUAL	= true;

	public static void main(String[] pUnused)
	{
		NetworkBuffer lBuffer		= null;
		MutexProcess[] lProcesses	= new MutexProcess[COMPONENTS];
		Thread[] lThreads			= new Thread[COMPONENTS];

		// First we create a fresh Registry for the duration of the program.
		try
		{
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		}
		// In case of an error, stop.
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		if (MANUAL)
		{
			lBuffer = new NetworkBuffer();
		}

		for (int i = 0; i < COMPONENTS; i++)
		{
			lProcesses[i] = new MutexProcess(MANUAL);
			lThreads[i]	  = new Thread(lProcesses[i]);
		}

		MutexView lFrame = new MutexView(lProcesses, lBuffer);
		lFrame.pack();
		lFrame.setVisible(true);

		if (!MANUAL)
		{
			for (int i = 0; i < COMPONENTS; i++)
			{
				lThreads[i].start();
			}
		}
	}
}