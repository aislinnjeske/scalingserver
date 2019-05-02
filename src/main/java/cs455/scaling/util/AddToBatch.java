package cs455.scaling.util;

import cs455.scaling.server.ThreadPoolManager;

public class AddToBatch implements Task{
	
	private ProcessData taskToBeAdded;
	private ThreadPoolManager threadPoolManager;
	
	public AddToBatch(ProcessData taskToBeAdded, ThreadPoolManager threadPoolManager) {
		this.taskToBeAdded = taskToBeAdded;
		this.threadPoolManager = threadPoolManager;
	}

	public void completeTask(){
		try {
			threadPoolManager.addTaskToBeBatched(taskToBeAdded);
		} catch(InterruptedException e) {
			System.out.println(e);
		}
	}
}
