package edu.upenn.cis.cis455.m1.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.cis.cis455.util.HttpParsing;

/**
 * Stub class for a thread worker for
 * handling Web requests
 */
public class HttpWorker extends Thread{
    private Thread t;
    private HttpTask myTask;
    private final int ThreadId;
    private final String threadName;
    private final HttpServer parentServer;
    private final HttpTaskQueue myQueue;
    private Socket taskSocket;
    
    public void run(){
    	while(parentServer.getServerStatus()== true){
    		try{
	            this.myTask=this.myQueue.deQueue();
	            if(parentServer.getServerStatus() == true){
		            this.parentServer.start(this);
		            this.taskSocket= this.myTask.getSocket();
		            HttpIoHandler myHandler= new HttpIoHandler(taskSocket, this.getThreadId(), parentServer);
	            }		        
			} catch (Exception e) {
				//e.printStackTrace();
			}
        	this.parentServer.done(this);
    	}
    }
    
    public String getThreadName(){
    	return this.threadName;
    }
    
    public int getThreadId(){
    	return this.ThreadId;
    }
    
    public void start(){
        //System.out.println("Starting " +  threadName );
        if (t == null && this.parentServer.getServerStatus() ==true) {
            t = new Thread (this, threadName);
            t.start ();
        }
        else{
        	t.interrupt();
        	//t=null;
        }
    }
    
    public HttpWorker(int id, HttpServer server) {
        this.ThreadId=id;
        this.threadName = "Thread "+id;
        //System.out.println("Creating " +  this.ThreadId );
        this.parentServer=server;
        this.parentServer.setThreadStatus(id, 0);
        this.parentServer.setThreadResource("Thread intialized; waiting to pick up task!", id);
        this.myQueue= server.getRequestQueue();
    }

}
