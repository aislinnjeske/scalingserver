package cs455.scaling.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class ServerStatistics extends TimerTask {
	
	private Server server;
	
	public ServerStatistics(Server server) {
		this.server = server;
	}

	public void run() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = new Date();
	    String dateString = formatter.format(now);
	    double[] stats = server.getStatistics();
		
	    System.out.printf("%s\t Server Throughput: %.3f messages/s, Active Client Connections: %d, Mean per-Client Throughput: %.3f messages/s, Std. Dev. of per-Client Throughput: %.3f messages/s\n",
	    				  dateString, stats[0], (int) stats[1], stats[2], stats[3]);
	}
}
