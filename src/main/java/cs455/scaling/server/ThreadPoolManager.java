package cs455.scaling.server;

import cs455.scaling.util.Batch;
import cs455.scaling.util.Task;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager {
	
	private int batchSize;
	private int batchTime;
	private int numberOfThreads;
	private long timeOfLastRemoval;
	private LinkedList<LinkedList<Task>> tasksToBeCompleted;
	private LinkedBlockingQueue<Task> tasksForThreads;
	
	public ThreadPoolManager(int numberOfThreads, int batchSize, int batchTime) {
		this.batchSize = batchSize;
		this.batchTime = batchTime;					//Batch time is in milliseconds
		this.numberOfThreads = numberOfThreads;
		this.timeOfLastRemoval = System.currentTimeMillis();
		this.tasksToBeCompleted = new LinkedList<>();
		this.tasksToBeCompleted.add(new LinkedList<>());
		this.tasksForThreads = new LinkedBlockingQueue<>();
	}

	public void createThreads(){
		for(int i = 0; i < numberOfThreads; i++) {
			Thread workerThread = new Thread(new WorkerThread(tasksForThreads));
			workerThread.start();
		}
	}

	public void sendTaskToThreadPool(Task task) {
		synchronized(tasksForThreads) {
			tasksForThreads.add(task);
			tasksForThreads.notify();
		}
	}

	public void addTaskToBeBatched(Task task) throws InterruptedException {
		synchronized(tasksToBeCompleted) {
			LinkedList<Task> list = tasksToBeCompleted.remove();
			list.add(task);
			tasksToBeCompleted.add(list);
					
			if(tasksToBeCompleted.peek().size() >= batchSize || (System.currentTimeMillis() - timeOfLastRemoval) >= batchTime) {
				Batch batch = new Batch(tasksToBeCompleted.remove());
				tasksToBeCompleted.add(new LinkedList<Task>());
				timeOfLastRemoval = System.currentTimeMillis(); 
				sendTaskToThreadPool(batch); 
			}
		}
	}
}
