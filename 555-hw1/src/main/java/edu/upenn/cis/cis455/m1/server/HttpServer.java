package edu.upenn.cis.cis455.m1.server;

import edu.upenn.cis.cis455.WebServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stub for your HTTP server, which
 * listens on a ServerSocket and handles
 * requests
 */
public class HttpServer implements ThreadManager {

	private final int server_port;
    private final String current_dir;
    private int maxQueueSize=20;
    private final int maxThreads;
    private HttpTaskQueue myHttpTaskQueue;
    protected int[] threadPoolStatus;
    private String[] threadResourse;
    private HttpWorker[] threads;
	private ServerSocket taskSocket; 
	private boolean serverStatus;
    
	public int getThreadCount(){
		return maxThreads;
	}
    
	public String getThreadStatus(int i){
		if( threadPoolStatus[i]==1){
			return "Busy";
		}
		return "Idle";
	}
	
	public void setThreadStatus(int i, int status){
		threadPoolStatus[i]=status;
//		if(status==-1){
//			this.threads[i].interrupt();
//			//System.out.println("thread"+i+" interrupted");
//		}
	}
	
	public void setThreadResource(String s, int i){
		//System.out.println("Setting thread"+ i+ "resourse to"+ s);
		if(this.threadPoolStatus[i]==1){
			//System.out.println("thread active");
		} else{
			//System.out.println("thread idle");
		}
		threadResourse[i]=s;
	}
	
	public String getThreadResource(int i){
		if( threadPoolStatus[i]!=-1){
			return threadResourse[i];
		}
		return "Invalid";
	}
	
    public HttpTaskQueue getRequestQueue() {
        //System.out.println("Returning my request queue");
        return this.myHttpTaskQueue;
    }
    
    public String getCurrentdir(){
    	return this.current_dir;
    }

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        return false;
    }

    public void start(HttpWorker worker) {
    	//System.out.println(worker.getThreadName()+" is active");
        this.setThreadStatus(worker.getThreadId(),1);
        this.setThreadResource("Waiting to parse request", worker.getThreadId());
    }

    public void done(HttpWorker worker) {
    	
    	if (this.threadPoolStatus[worker.getThreadId()] != -1){
    		//System.out.println(worker.getThreadName()+" is done");
    		this.setThreadStatus(worker.getThreadId(),0);
    		this.setThreadResource("Waiting for socket", worker.getThreadId());
    	}
    }

    public void error(HttpWorker worker) {
    	//System.out.println(worker.getThreadName()+" errored");
    	this.setThreadStatus(worker.getThreadId(),-1);
    }
    
    
    public HttpServer(int server_port, String rootDir, int max_threads){
		this.setServerStatus(true);
        //System.out.println("Starting Http Server!");
        this.server_port=server_port;
        this.current_dir= rootDir;
        this.maxThreads=max_threads;
        this.threadPoolStatus= new int[max_threads];
        this.threadResourse= new String[max_threads];
        this.threads= new HttpWorker[max_threads];
        //create a HttpQueue
        this.myHttpTaskQueue= new HttpTaskQueue(this.maxQueueSize);
        try {
			this.taskSocket = new ServerSocket(server_port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
        //intialize the threads
	    for (int i=0; i<this.maxThreads; i++) {
			HttpWorker thread = new HttpWorker(i, this);
			threads[i]=(thread);
			thread.start();
		}
	    
	    try {
        	//Removed ServerSocket declaration from here made it local
        	
            //System.out.println("http Server created new socket");
            while(this.getServerStatus()== true){
            	//System.out.println("Entering loop again "+getServerStatus());
                Socket socketRequest = taskSocket.accept();
                HttpTask myTask= new HttpTask(socketRequest);
                try {
                    myHttpTaskQueue.enQueue(myTask);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        }  catch (IOException e) {
            //e.printStackTrace();  
            if(this.getServerStatus() != true){
	            for (int i=0; i<this.maxThreads; i++) {
	            	if(threadPoolStatus[i] != -1){
		            	this.threads[i].interrupt();
		            	System.out.println("interrupt"+ i);
	            	}
	    		}
            } else{
            	for (int i=0; i<this.maxThreads; i++) {
	            	if(threadPoolStatus[i] == 0){
		            	//System.out.println(threads[(i)].getThreadName());
		            	this.done(threads[(i)]);
	            	}
	    		}
            }
        }
	    
        try {
        	for (int i=0; i<this.maxThreads; i++) {
        		//if(threadPoolStatus[i] != -1){
	        		this.threads[i].interrupt();
	        		this.threads[i].join();
            	//}
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    }

	public boolean getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(boolean serverStatus) {
		this.serverStatus = serverStatus;
		//System.out.println("Setting Server status to "+ serverStatus);
		if(this.getServerStatus() != true){
			try {
				this.taskSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            for (int i=0; i<this.threadPoolStatus.length; i++) {
            	if(threadPoolStatus[i] == 0){
	            	//System.out.println(threads[(i)].getThreadName());
	            	//this.done(threads[(i)]);
	            	//this.threads[i].interrupt();
	            	this.setThreadStatus(i, -1);
            	}
    		}
        }
	}
}