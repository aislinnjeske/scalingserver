package cs455.scaling.client;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

public class ClientStatistics extends TimerTask{
	
	private Client client;
	
	public ClientStatistics(Client client) {
		this.client = client;
	}

	public void run() {
		int[] stats = client.getStatistics();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = new Date();
	    String dateString = formatter.format(now);
		
		System.out.printf("%s\tTotal Sent Count: %d, Total Received Count: %d\n", dateString, stats[0], stats[1]);
	}
}
