package cs455.scaling.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import cs455.scaling.util.Hash;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;


public class Client {
	
	private String serverHostname;
	private int serverPortNumber;
	private int messageRate;
	private int messagesSent;
	private int messagesReceived;
	private Selector selector;
	private Hash hasher;
	private static SocketChannel socketChannel;
	private LinkedList<String> hashes;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		InetAddress ip = InetAddress.getLocalHost();
		String hostname = ip.getHostName();
		
		System.out.printf("Running client on %s\n", hostname);
		
		Client client = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		client.initialize();
	}
	
	public Client(String serverHostname, int serverPortNumber, int messageRate) throws IOException {
		this.serverHostname = serverHostname;
		this.serverPortNumber = serverPortNumber;
		this.messageRate = messageRate;
		
		hashes = new LinkedList<>();
		hasher = new Hash();
		selector = Selector.open();
	}
	
	public void initialize() throws IOException, InterruptedException {
		createSocketChannel();
		startStatsTimer();
		startSendingMessages();
		manageSocketChannel();
	}
	
	private void createSocketChannel() throws IOException {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(serverHostname, serverPortNumber));
		socketChannel.register(selector, SelectionKey.OP_READ);
		
		while(!socketChannel.finishConnect()) {
			socketChannel.finishConnect();
		}
	}
	
	private void startStatsTimer() {
		Timer timer = new Timer();
		timer.schedule(new ClientStatistics(this), 20000, 20000);
	}
	
	private void startSendingMessages() {
		Thread messageSenderThread = new Thread(new SenderThread(this, socketChannel, hashes, messageRate));
		messageSenderThread.start();
	}
	
	private void manageSocketChannel() throws IOException, InterruptedException {
		while(true) {
			//Will block until there's some activity
			selector.select();
			
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectedKeys.iterator();
			

			while(iter.hasNext()) {
				SelectionKey key = iter.next();

				if(key.isReadable()) {
					read(key);
				}
				iter.remove();
			}
		}
	}
	
	private void read(SelectionKey key) {
		messagesReceived++; 
		String hashReceived = readIncomingMessage((SocketChannel) key.channel());
		
		synchronized(hashes) {
			hashes.remove(hashReceived); 
		}
	}

	public String readIncomingMessage(SocketChannel clientChannel) {
		ByteBuffer buff = ByteBuffer.allocate(8000);
		
		try {
			int read = 0;
			while(buff.hasRemaining() && read != -1 ) {
				read = clientChannel.read(buff);
			}
			
			byte[] message = buff.array();
			String hash = hasher.getHash(message);
			
			return hash;

		} catch(IOException e) {
			System.out.println(e);
			return null;
		}
	}

	public synchronized int[] getStatistics() {
		int[] stats = {messagesSent, messagesReceived};
		messagesSent = 0;
		messagesReceived = 0; 
		return stats;
	}

	public synchronized void sentMessage() {
		messagesSent++;
	}
}
