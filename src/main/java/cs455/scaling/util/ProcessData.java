package cs455.scaling.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ProcessData implements Task {
	
	private SelectionKey key;
	
	public ProcessData(SelectionKey key) {
		this.key = key;
	}

	public void completeTask() {
		SocketChannel clientChannel = (SocketChannel) key.channel();
		
		byte[] data = readData(clientChannel);
		sendData(clientChannel, data); 
	}
	
	private byte[] readData(SocketChannel clientChannel) {
		ByteBuffer buff = ByteBuffer.allocate(8000);
		
		try {
			
			int read = 0;
			while(buff.hasRemaining() && read != -1) {
				read = clientChannel.read(buff);
			}
			
			//Turn reading back on for this key
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			
			return buff.array();
			
		} catch(IOException e) {
			System.out.println(e);
			return null;
		}
	}
	
	private void sendData(SocketChannel clientChannel, byte[] data) {
		ByteBuffer buff = ByteBuffer.wrap(data);
		
		while(buff.hasRemaining()) {
			try {
				clientChannel.write(buff);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}