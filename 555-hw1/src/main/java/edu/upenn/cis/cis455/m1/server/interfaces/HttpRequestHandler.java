package edu.upenn.cis.cis455.m1.server.interfaces;

import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.interfaces.Request;
import edu.upenn.cis.cis455.m1.server.interfaces.Response;

@FunctionalInterface
public interface HttpRequestHandler {
	//Do all the checks to validate the request object populate the response object
	
    public void handle(Request request, Response response) throws HaltException;
}
