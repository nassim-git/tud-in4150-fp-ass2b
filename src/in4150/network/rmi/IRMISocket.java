package in4150.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRMISocket extends Remote{
	public void receive(RMIAddress from, RMIMessage msg) throws RemoteException;
}
