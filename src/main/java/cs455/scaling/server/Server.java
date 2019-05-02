package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import cs455.scaling.util.AddToBatch;
import cs455.scaling.util.ProcessData;
import cs455.scaling.util.Register;

public class Server {
	
	private int portNumber;
	private int threadPoolSize;
	private int batchSize;
	private int batchTime;
	private static Selector selector;
	private int serverThroughput;
	private Map<SocketChannel, Integer> clientMessageCounts;
	private static ServerSocketChannel serverSocketChannel;
	private static ThreadPoolManager manager;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Double.parseDouble(args[3]));
		server.initialize();
	}
	
	public Server(int portNumber, int threadPoolSize, int batchSize, double batchTime) {
		this.portNumber = portNumber;
		this.threadPoolSize = threadPoolSize;
		this.batchSize = batchSize;
		this.batchTime = (int) batchTime * 1000;
		
		serverThroughput = 0;
		clientMessageCounts = new HashMap<>();
	}
	
	public void initialize() throws IOException, InterruptedException {
		createThreadPool();
		createServerSocketChannel();
		startStatsTimer();
		manageSocketServer();
	}
	
	private void createThreadPool() {
		manager = new ThreadPoolManager(threadPoolSize, batchSize, batchTime);
		manager.createThreads();
	}
	
	private void createServerSocketChannel() throws IOException {
		selector = Selector.open();
		
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(portNumber));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	private void startStatsTimer() {
		Timer timer = new Timer();
		timer.schedule(new ServerStatistics(this), 20000, 20000);
	}
	
	private void manageSocketServer() throws IOException, InterruptedException {
		while(true) {
			selector.selectNow();					//not blocking
			
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectedKeys.iterator();
			
			while(iter.hasNext()) {
				SelectionKey key = iter.next();

				if(key.isAcceptable()) {
					accept(key);
				} else if(key.isReadable()) {
					read(key); 
				}
				iter.remove();
			}
		}
	}
	
	private void accept(SelectionKey key) {
		key.interestOps(key.interestOps() & ~SelectionKey.OP_ACCEPT);
		
		Register task = new Register(this, key);
		manager.sendTaskToThreadPool(task);
	}
	
	private void read(SelectionKey key) {
		SocketChannel clientChannel = (SocketChannel) key.channel();
		
		synchronized(this) {
			serverThroughput++;
		}
		
		synchronized(clientMessageCounts) {
			int count = clientMessageCounts.get(clientChannel);
			clientMessageCounts.put(clientChannel, ++count);
		}
		
		ProcessData task = new ProcessData(key);
		key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
		
		AddToBatch addToBatch = new AddToBatch(task, manager);
		manager.sendTaskToThreadPool(addToBatch);	
	}
	
	public void acceptIncomingConnection(SelectionKey key) {
		try {
			SocketChannel client = serverSocketChannel.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
			
			synchronized(clientMessageCounts) {
				clientMessageCounts.put(client, 0);
			}
			
			key.interestOps(key.interestOps() | SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public synchronized double[] getStatistics() {
		double[] stats = new double[4];
		
		synchronized(clientMessageCounts) {
			stats[0] = serverThroughput / 20.0;
			serverThroughput = 0;
			stats[1] = clientMessageCounts.size();
			stats[2] = getClientThroughputAverage() / 20.0;
			stats[3] = getClientThroughputStdDev(getClientThroughputAverage());
		}
		return stats;
	}
	
	private double getClientThroughputAverage() {
		double sum = 0;
		for(SocketChannel s : clientMessageCounts.keySet()) {
			sum += clientMessageCounts.get(s);
		}
		return sum / clientMessageCounts.size();
	}
	
	private double getClientThroughputStdDev(double average) {
		double stdDev = 0;
		
		for(SocketChannel s : clientMessageCounts.keySet()) {
			stdDev += Math.pow(clientMessageCounts.get(s) - average, 2);
			clientMessageCounts.put(s, 0);
		}
		
		return Math.sqrt(stdDev / clientMessageCounts.size()) / 20.0;
	}
}
