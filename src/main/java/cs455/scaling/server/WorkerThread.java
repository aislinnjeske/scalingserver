package cs455.scaling.server;

import java.util.concurrent.LinkedBlockingQueue;

import cs455.scaling.util.Task;

public class WorkerThread implements Runnable {
	
	private LinkedBlockingQueue<Task> queue;
	
	public WorkerThread(LinkedBlockingQueue<Task> queue) {
		this.queue = queue;
	}

	public void run() {
		while(true) {
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
						System.out.println(e);
					}
				}
				
				Task task = queue.poll();
				
				if(task != null) {
					task.completeTask();
				}				
			}
		}
	}
}
