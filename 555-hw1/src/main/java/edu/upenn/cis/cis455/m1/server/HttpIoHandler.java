package edu.upenn.cis.cis455.m1.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static edu.upenn.cis.cis455.ServiceFactory.*;

import edu.upenn.cis.cis455.ServiceFactory;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.interfaces.HttpRequestHandler;
import edu.upenn.cis.cis455.m1.server.interfaces.Request;
import edu.upenn.cis.cis455.m1.server.interfaces.Response;
import edu.upenn.cis.cis455.util.HttpParsing;

/**
 * Handles marshalling between HTTP Requests and Responses
 */
public class HttpIoHandler {
	
	private boolean isValid = false;
	private String currentDir;
	public static final String endline = "\n\r";
	
	
    final static Logger logger = LogManager.getLogger(HttpIoHandler.class);

    /**
     * Sends an exception back, in the form of an HTTP response code and message.  Returns true
     * if we are supposed to keep the connection open (for persistent connections).
     */
    public static boolean sendException(Socket socket, Request request, HaltException except) {
        return false;
    }

    /**
     * Sends data back.   Returns true if we are supposed to keep the connection open (for 
     * persistent connections).
     */
    public static boolean sendResponse(Socket socket, Request request, Response response) {
        return true;
    }
    
    private void buildErrorResponse(HttpResponse myResponse){
    	//System.out.println("Building Error Response");
    	StringBuilder errorBody = new StringBuilder();
    	myResponse.type("text/html");
    	errorBody.append("<!doctype html>");
    	errorBody.append("<html lang=\"en\">");
    	errorBody.append("<head>");
    	errorBody.append("<meta charset=\"utf-8\">");
    	errorBody.append("<title>Page Not Found</title>");
    	errorBody.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
    	errorBody.append("<style>* {line-height: 1.2;margin: 0;}");
    	errorBody.append("html {color: #888;display: table;font-family: sans-serif;height: 100%;text-align: center;width: 100%;}");
    	errorBody.append("body {display: table-cell;vertical-align: middle;margin: 2em auto;}");
    	errorBody.append("h1 {color: #555;font-size: 2em;font-weight: 400;}");
    	errorBody.append("p {margin: 0 auto;width: 280px;}");
    	errorBody.append("@media only screen and (max-width: 280px) {");
    	errorBody.append("body, p {width: 95%;}");
    	errorBody.append("h1 {font-size: 1.5em;margin: 0 0 0.3em;}}");
    	errorBody.append("</style></head><body>");
    	if(myResponse.status() == 404){
	    	errorBody.append("<h1>Page Not Found</h1>");
	    	errorBody.append("<p>Sorry, but the page you were trying to view does not exist.</p>");
    	} else{
    		errorBody.append("<h1>"+HttpParsing.explainStatus(myResponse.status())+"</h1>");
	    	errorBody.append("<p>Sorry, Something seems to have gone worng, Please fretry again.</p>");
    	}
    	errorBody.append("</body>");
    	errorBody.append("</html>"+endline);
    	myResponse.body(errorBody.toString());
    	myResponse.contentLength= myResponse.body().length();
    }
    
    private void buildResponse(HttpRequest myRequest, HttpResponse myResponse){
    	//System.out.println("Building Response");
    	if (myResponse.status() != 200){
			buildErrorResponse(myResponse);
		}
    	StringBuilder response = new StringBuilder();
		response.append(endline+myRequest.protocol());
    	response.append(" " + Integer.toString(myResponse.status())+" "+HttpParsing.explainStatus(myResponse.status())+endline);
		myResponse.response_first=response;
		StringBuilder head = new StringBuilder();
		head.append("Content-Length: "+myResponse.contentLength+endline);
		head.append("Content-Type: "+myResponse.type()+endline);
		head.append("Connection: Closed" + endline+ endline);
		myResponse.response_head=head;
		
    }
    
    private boolean validateRequest(HttpRequest request, HttpResponse response){
    	try{
//    	 StringBuilder response_first = new StringBuilder();
//			StringBuilder response_head = new StringBuilder();
//			StringBuilder response_body = new StringBuilder();
//			response_first.append(myRequest.protocol());
//			response_first.append(" " + Integer.toString(myResponse.status()));
//			response_first.append(" OK\n\r");
	    	if( request.protocol().equalsIgnoreCase("HTTP/1.0")){
	    		//System.out.println("HTTP/1.0 Not Support Absolute Path: 404 Not Found\n");
	    		response.response_first.append(request.protocol()+" 404 BADREQUEST\r\n\n");
	    		return false;
	    	}
	    	else if ( request.protocol().equalsIgnoreCase("HTTP/1.1")){
	    		if(request.host()==null){
	    			//System.out.println("No host found");
	    			response.response_first.append(request.protocol()+" 400 BADREQUEST\r\n\n");
	        		return false;
	    		}
	    		else{
	    			if (!(request.requestMethod().equalsIgnoreCase("GET") || request.requestMethod().equalsIgnoreCase("HEAD"))){
	    				return false;
	    			}
	    		}
	    	}
	    	
    	} catch (Exception e){
    		//e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    public HttpIoHandler (Socket taskSocket, int threadId, HttpServer myServer){
    	
    	setCurrentDir(myServer.getCurrentdir());
    	try {
			InputStreamReader reader = new InputStreamReader(taskSocket.getInputStream());
			BufferedReader bufferReader = new BufferedReader(reader);
	        Map<String, String> pre = new HashMap<String, String>();
	        Map<String, List<String>> parms = new HashMap<String, List<String>>();
	        Map<String, String> headers = new HashMap<String, String>();
	        HttpParsing.decodeHeader(bufferReader, pre, parms, headers);
	        //I got the objects pre params, Headers Now i have to create a request object
	        //Request myRequest = ServiceFactory.createRequest(taskSocket,pre.get("uri"),false,headers,parms);
	        HttpRequest myRequest = new HttpRequest(pre, parms, headers, getCurrentDir());
	        myServer.setThreadResource(myRequest.uri(), threadId);
	        HttpResponse myResponse = new HttpResponse();
	        //validate the request if it's a valid request send to RequestHandler if not send error response
	        RequestHandler myRequestHandler= new RequestHandler();
	        
	        OutputStream out = taskSocket.getOutputStream();
	        
	        //Validate the request object before sending to RequestHandler;
	        //If invalid don't send to request Handler send error response back directly
	        isValid= validateRequest(myRequest, myResponse);
	        if (isValid== true){
	        	myRequestHandler.handleRequest(myRequest, myResponse, myServer);
	        	buildResponse(myRequest, myResponse);
	        }
	        
	        
// Temp-----------------------------------------------------------------
//	        System.out.println("Written output stream : "+ myResponse.toString());
//	        out.write(("HTTP/1.1 200 OK\n\rDate: Mon, 27 Jul 2009 12:28:53 GMT\n\rServer: Apache/2.2.14 (Win32)\n\rLast-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n\rContent-Length: 88\n\rContent-Type: text/html\n\rConnection: Closed\n\r<html>\n\r<body>\n\r<h1>Hello, World!</h1>\n\r</body>\n\r</html>").getBytes());
//	        myResponse.response_first.append(myRequest.protocol());
//	        myResponse.response_first.append(" " + Integer.toString(myResponse.status()));
//	        myResponse.response_first.append(" OK\n\r");
//			for(String key : myRequest.headers()){
//				response_head.append(key+": ");
//				response_head.append(myRequest.headers.get(key));
//				response_head.append("\n\r");		
//			}
//			response_head.append("Date: Mon, 27 Jul 2009 12:28:53 GMT\n\rServer: Apache/2.2.14 (Win32)\n\rLast-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n\rContent-Length: 88\n\rContent-Type: text/html\n\rConnection: Closed\n\r\n\r");
// Temp-----------------------------------------------------------------

	        try{
//		        System.out.println("firstLine: "+(myResponse.response_first.toString()));
//		        System.out.println("head: "+(myResponse.response_head.toString()));
//		        System.out.println("body: "+(myResponse.bodyRaw()));
		        out.write(myResponse.response_first.toString().getBytes());
				out.write(myResponse.response_head.toString().getBytes());
				if (myRequest.requestMethod().equalsIgnoreCase("GET")){
					out.write(myResponse.bodyRaw());
				}
		        //System.out.println("Written output stream");
	        } catch (Exception e) {
	        	e.printStackTrace();
			}
	        try {
	        	taskSocket.close();
	        } catch (IOException e) { /* failed */ }
	        //Got back the response Now send it back!
	        //System.out.println("Woouza");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}   
    }

	public String getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(String currentDir) {
		this.currentDir = currentDir;
	}
}
