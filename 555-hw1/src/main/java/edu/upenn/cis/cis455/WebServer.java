package edu.upenn.cis.cis455;
import edu.upenn.cis.cis455.m1.server.HttpServer;
import edu.upenn.cis.cis455.m1.server.interfaces.WebService;
import edu.upenn.cis.cis455.handlers.Route;

import org.apache.logging.log4j.Level;
// Main program that i call to start everything
// called using a command line arg
// should parse it, find the directory, port and other shit
// should probably call WebServiceController
// and pass the control to it.


public class WebServer extends WebService {
    
	private int server_port;
    private String current_dir;
    private String ip_Address;
    public static int max_threads=5;
    
    public void port(int port){
        this.server_port=port;
    }
    
    public void threadPool(int threads){
        this.max_threads = threads; 
    }
    public void ipAddress(String ipAddress){
        this.ip_Address=ipAddress;
    }

    public void staticFileLocation(String directory){
        this.current_dir = directory;
    }

    @Override
    public void get(String path, Route route){}
    
    @Override
    public void start(){
       HttpServer myHttpServer = new HttpServer(this.server_port,this.current_dir,this.max_threads);
    }
    
    @Override
    public void stop(){
        
    }
    
    public static void main(String[] args) {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
    
        //printing to see input ags
        int server_port=8080;
        String current_dir="./www";
        if (args != null){
            try {
                server_port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
            current_dir=args[1];
        }
        
        //System.out.println( server_port+" okay "+ current_dir);
        WebServer myServer= new WebServer();
        myServer.port(server_port);
        myServer.ipAddress("0.0.0.0");
        myServer.staticFileLocation(current_dir);
        myServer.start();
        //System.out.println("Done!");
    }
}