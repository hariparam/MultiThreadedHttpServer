package edu.upenn.cis.cis455.m1.server;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Stub class for implementing the queue of HttpTasks
 */
public class HttpTaskQueue {
    
	//Should initialize an array of HTTP tasks to store in the queue
    private Vector<HttpTask> sharedQueue;
    //should contain a variable to store the size of the queue
    private int queueSize;

	public HttpTaskQueue(int QueueSize) {
		//create a queue 
		//System.out.println("Initializng Task Queue!");
		this.sharedQueue = new Vector<HttpTask>(QueueSize);
		this.queueSize = QueueSize;		
	}
    
    public void enQueue(HttpTask myTask) throws InterruptedException {
		//wait if the queue is full
		//System.out.println("New Task: calling enqueue!");
		while (sharedQueue.size() == this.queueSize) {
			// Synchronizing on the sharedQueue to make sure no more than one
			// thread is accessing the queue same time.
			synchronized (sharedQueue) {
				//System.out.println("Queue is full!");
				sharedQueue.wait();
				// We use wait as a way to avoid polling the queue to see if
				// there was any space for the producer to push.
			}
		}

		synchronized (sharedQueue) {
			sharedQueue.add(myTask);
			//Who's that all?? All the threads that are waiting on this.
			sharedQueue.notify();
		}
	}
	
	public HttpTask deQueue() throws InterruptedException {
		//return the first socket and delete it
		//System.out.println("Calling de queue!");
		HttpTask taskInQueue;
		while (sharedQueue.isEmpty()) {
			//If the queue is empty, we push the current thread to waiting state. Way to avoid polling.
			synchronized (sharedQueue) {
				//System.out.println("Queue is currently empty ");
				sharedQueue.wait();
			}
		}

		synchronized (sharedQueue) {
		    taskInQueue=sharedQueue.remove(0);
			sharedQueue.notify();
			return taskInQueue;
		}
	}
}
