package cs455.scaling.client;

import cs455.scaling.util.Hash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Random;

public class SenderThread implements Runnable {

	private Client client;
	private int messageRate;
	private Random random;
	private Hash sha1;
	private SocketChannel socketChannel;
	private LinkedList<String> hashes;
	
	public SenderThread(Client client, SocketChannel socketChannel, LinkedList<String> hashes, int messageRate) {
		this.client = client;
		this.messageRate = messageRate;
		this.socketChannel = socketChannel;
		this.hashes = hashes;
		random = new Random();
		sha1 = new Hash();
	}
	
	public void run() {
		
		while(true) {
			
			byte[] payload = createPayload();
			
			String hash = sha1.getHash(payload);
			
			synchronized(hashes) {
				hashes.add(hash);
			}
			
			sendMessageToServer(payload);
			client.sentMessage();
			
			try {
				Thread.sleep(1000 / messageRate);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
	}
	
	private byte[] createPayload() {
		byte[] payload = new byte[8000];
		random.nextBytes(payload);
		return payload;
	}
	
	public void sendMessageToServer(byte[] payload){
		ByteBuffer byteBuffer = ByteBuffer.wrap(payload);

		while(byteBuffer.hasRemaining()) {
			try {
				socketChannel.write(byteBuffer);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
	
}
