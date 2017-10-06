package edu.upenn.cis.cis455.m1.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.upenn.cis.cis455.m1.server.interfaces.Request;;

public class HttpRequest extends Request{
	
	private String method;
	private String host;
	private String userAgent;
	private int port;
	private String pathInfo;
	private String url;
	private String uri;
	private String protocol;
	private String contentType;
	private String ip;
	private String body;
	private int contentLength;
	private String currDir;
	public Map<String, String> headers;
	

	//private Boolean persistent= false;
	
	
	 /**
     * The request method (GET, POST, ...)
     */
	@Override
	public String requestMethod() {
		// TODO Auto-generated method stub
		return this.method;
	}
	/**
     * @return The host
     */
	@Override
	public String host() {
		// TODO Auto-generated method stub
		return this.host;
	}

	@Override
	public String userAgent() {
		// TODO Auto-generated method stub
		return this.userAgent;
	}

	@Override
	public int port() {
		// TODO Auto-generated method stub
		return this.port;
	}

	@Override
	public String pathInfo() {
		// TODO Auto-generated method stub
		return this.pathInfo;
	}

	@Override
	public String url() {
		// TODO Auto-generated method stub
		return this.url;
	}

	@Override
	public String uri() {
		// TODO Auto-generated method stub
		return this.uri;
	}

	@Override
	public String protocol() {
		// TODO Auto-generated method stub
		return this.protocol;
	}

	@Override
	public String contentType() {
		// TODO Auto-generated method stub
		return this.contentType;
	}

	@Override
	public String ip() {
		// TODO Auto-generated method stub
		return this.ip;
	}

	@Override
	public String body() {
		// TODO Auto-generated method stub
		return this.body;
	}

	@Override
	public int contentLength() {
		// TODO Auto-generated method stub
		return this.contentLength;
	}

	@Override
	public String headers(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> headers() {
		// TODO Auto-generated method stub
		return headers.keySet();
	}
	
//	 Map<String, String> pre = new HashMap<String, String>();
//     Map<String, List<String>> parms = new HashMap<String, List<String>>();
//     Map<String, String> headers = new HashMap<String, String>();
	public HttpRequest( Map<String, String> pre, Map<String, List<String>> parms, Map<String, String> headers, String currDir){
		//Trying to see what the hell i have here
//		System.out.println("KeySet Pre");
//		for( String key: pre.keySet()){
//			System.out.println("key: "+ key+" value: "+ pre.get(key));
//		}
//		System.out.println("KeySet param");
//		for( String key: parms.keySet()){
//			System.out.println("key: "+ key+" value: "+ parms.get(key));
//		}
//		System.out.println("KeySet Headers");
//		for( String key: headers.keySet()){
//			System.out.println("key: "+ key+" value: "+ headers.get(key));
//		}
		
		setCurrDir(currDir);
		//Populating the values
		try{
			this.method = pre.get("method");
			this.host = headers.get("host").split(":")[0];
			this.userAgent=headers.get("user-agent");
			this.port = Integer.valueOf(headers.get("host").split(":")[1]);
			//System.out.println(getCurrDir()+pre.get("uri"));
			this.pathInfo=getCurrDir()+pre.get("uri");
			this.uri = pre.get("uri");
			this.protocol = pre.get("protocolVersion");
			this.headers = headers;
			//		this.contentType;
			//		this.ip;
			//		this.body;
			//		this.contentLength;
			//		this.url= this.host+":"+this.port+"/?";

		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	public String getCurrDir() {
		return currDir;
	}
	public void setCurrDir(String currDir) {
		this.currDir = currDir;
	}
}
