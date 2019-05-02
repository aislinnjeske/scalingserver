package cs455.scaling.util;

import java.nio.channels.SelectionKey;

import cs455.scaling.server.Server;

public class Register implements Task {
	
	private Server server;
	private SelectionKey key;
	
	public Register(Server server, SelectionKey key) {
		this.server = server;
		this.key = key;
	}

	public void completeTask() {
		server.acceptIncomingConnection(key);
	}
}
