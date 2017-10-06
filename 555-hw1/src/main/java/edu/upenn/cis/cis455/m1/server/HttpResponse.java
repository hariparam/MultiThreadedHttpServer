package edu.upenn.cis.cis455.m1.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import edu.upenn.cis.cis455.m1.server.interfaces.Response;

public class HttpResponse extends Response{
	
	public StringBuilder response_first;
	public StringBuilder response_head;
	public StringBuilder response_body;
	public int contentLength=0;
	private String protocol; 
	private String method;
	private String responseDate;
	
	public HttpResponse() {
		StringBuilder response_first = new StringBuilder();
		StringBuilder response_head = new StringBuilder();
		StringBuilder response_body = new StringBuilder();
	}
	
	@Override
	public String getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getResponseDate() {
		return responseDate;
	}

	public void setResponseDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.responseDate= dateFormat.format(calendar.getTime());
	}

}
