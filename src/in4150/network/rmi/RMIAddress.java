package in4150.network.rmi;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;




public class RMIAddress implements Serializable{

	private static final long serialVersionUID = 4325567851385273037L;
	public String host = "localhost";
	public int port = 1099;
	public String objectname;
	
	public RMIAddress(String objectname){
		this.host = RMIAddress.getIp();
		this.objectname = objectname;
	}
	public RMIAddress(String host,String objectname){
		this.host = host;
		this.objectname = objectname;
	}
	
	public static String getIp(){
		String myIp = "";
		try{
			myIp = InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException e){
			// TODO something useful with the exception
			e.printStackTrace();
			System.exit(0);
		}
		return myIp;
	}
}
