package edu.upenn.cis.cis455.m1.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimeZone;
import javax.imageio.stream.FileImageInputStream;

import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.interfaces.HttpRequestHandler;
import edu.upenn.cis.cis455.m1.server.interfaces.Request;
import edu.upenn.cis.cis455.m1.server.interfaces.Response;
import edu.upenn.cis.cis455.util.HttpParsing;

public class RequestHandler implements HttpRequestHandler {
	public static final String endline = "\n\r";

	// To check the validity and authentication to access the file
	private boolean validateFileAccess(String filename){
		//System.out.println("Validating "+filename);
		ArrayList<String> Levels= new ArrayList<String>(Arrays.asList(filename.split("/")));
		int count=0;
		for(String level : Levels){
//			System.out.println(level+level.length()+Integer.toString(count));
//			System.out.println(level+Integer.toString(count));
			if(level.equals("..")){
				if(count==0){
					System.out.println("Access denied");
					return false;
				}
				else{
					count=count-1;
				}
			}
			else{
				count=count+1;
			}
		}
		return true;
	}
	
	@Override
	public void handle(Request request, Response response) throws HaltException {
		
	}
	
	public String controlResponse(HttpServer myServer){
		StringBuilder body= new StringBuilder();
		body.append("<!DOCTYPE html><html><head><title>Control Panel</title></head><body><table><tr><th>Thread</th><th>Status</th><th>Process using</th></tr>");
		for( int i=0; i <myServer.getThreadCount(); i++ ){
			body.append("<tr><th>	Thread_"+i+"</th><th>"+myServer.getThreadStatus(i)+"</th><th>"+myServer.getThreadResource(i)+"</th></tr>");
		}
		body.append("</table>");
		body.append("<ul><li><a href=\"/shutdown\">Shut down</a></li></ul>");
		body.append("</body></html>"+endline);
		return body.toString();
	}
	
	public String shutdownResponse(){
		StringBuilder body= new StringBuilder();
		body.append("<!DOCTYPE html><html><head><title>Shutdown</title></head><body>");
		body.append("<ul><li><a>Shut down in progress waiting fot all threads to finish. Refresh browser to confirm</a></li></ul>");
		body.append("</body></html>"+endline);
		return body.toString();
	}
	
	
	
	public void handleRequest(HttpRequest request, HttpResponse response, HttpServer myServer) throws HaltException {
		// TODO Auto-generated method stub
		// Handle 4 cases
		///shutdown; /control panel / dir /file
		//String fullPath=('')
		if(request.requestMethod().equalsIgnoreCase("GET")) {
            File file = new File(request.pathInfo());
//            System.out.println(file.listFiles()+".");
            //System.out.println(request.pathInfo()+" " +file.isDirectory()+" ;"+ file.isFile());
            if(request.protocol() != null){
            	response.setProtocol(request.protocol());
            }
            if(request.requestMethod() != null){
            	response.setMethod(request.requestMethod());
            }
            
           response.setResponseDate();
           //System.out.println(request.uri());
            if(request.uri().equalsIgnoreCase("/control")) {
// to update    
            	//System.out.println("\\control");
            	response.type("text/html");   
            	response.status(200);
            	response.body(controlResponse(myServer));
            	response.contentLength=response.body().length();
            }
            else if(request.uri().equalsIgnoreCase("/shutdown")) {
// to update                generateShutdownHtml(outputHtml);
            	//System.out.println("\\shutdown");
            	response.status(200);
            	response.type("text/html");
            	myServer.setServerStatus(false);
            	response.body(shutdownResponse());
            	response.contentLength=response.body().length();
            }
            else if(file.isDirectory()) {
            	if (validateFileAccess(request.uri())){
	            	//System.out.println("Is directory");
	            	response.type("text/html");
	            	StringBuilder body = new StringBuilder();
	            	body.append("<!DOCTYPE html>\r\n");
	            	body.append("<html><head><meta charset='utf-8' /><title>");
	            	body.append("File Listing in " + request.uri());
	            	body.append("</title></head><body>\r\n");
	            	body.append("<ul><li><a href=\"../\">..</a></li>\r\n");
	            	for ( File f: file.listFiles()){
	            		if (f.isHidden()|| !f.canRead()){
	            			continue;
	            		}
	            		String name = f.getName();
	            		body.append("<li><a href=\"" + name + "\">" + name + "</a></li>\r\n");
	            	}
	            	body.append("</ul></body></html>\r\n");
	            	response.body(body.toString());
	            	//System.out.println(response.response_body);
	            	response.contentLength=response.body().length();
	            } else{
                    //System.out.println("file access Denied");
                    response.status(403);
                }
            }
            else {
            	//System.out.println("Is File");
            	if (validateFileAccess(request.uri())){
            		if (file.isFile()){
            			byte[] body = new byte[(int) file.length()];
            			InputStream stream = null;
            			
            			try {
							stream = new FileInputStream(file);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	            		try {
	            			stream.read(body);
	            			stream.close();
	            			response.bodyRaw(body);
							//response.bodyRaw(Files.readAllBytes(file.toPath()));
							response.contentLength = (int) file.length(); 
							response.type(HttpParsing.getMimeType(request.pathInfo()));
							//response.type(HttpParsing.getMimeType(file.toString()));
							response.status(200);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
            		}
            		else {
                        //System.out.println("File Not Exist: 404 Not Found\n");
                        response.status(404);
                    }
            	}
                else{
                    //System.out.println("file access Denied");
                    response.status(403);
                }
            	
            }
        }
	}
}
