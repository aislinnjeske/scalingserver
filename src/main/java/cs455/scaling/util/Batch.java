package cs455.scaling.util;

import java.util.LinkedList;

public class Batch implements Task {
	
	private LinkedList<Task> batch;
	
	public Batch(LinkedList<Task> batch) {
		this.batch = batch;
	}

	public void completeTask() {
		for(Task t : batch) {
			t.completeTask();
		}
	}
}
