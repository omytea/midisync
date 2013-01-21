package com.omt.midisync;

//import java.io.IOException;
//import javax.servlet.http.*;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

/**
 * Resource which has only one representation.
 * 
 */
public class HelloWorldResource extends ServerResource {

	@Get
	public String represent() {
		Request request = getRequest();

		Form form = request.getResourceRef().getQueryAsForm();
		String retStr = "";
		for (Parameter parameter : form) {
			System.out.print("parameter " + parameter.getName());
			System.out.println("=" + parameter.getValue());
			retStr= retStr+ parameter.getValue();
		}

		return retStr;
	}
	
	   @Put
	    public String put() {
	    	Request request= getRequest(); 
	    	
	    	Form form = request.getResourceRef().getQueryAsForm();
	    	for (Parameter parameter : form) {
	    	  System.out.print("parameter " + parameter.getName());
	    	  System.out.println("=" + parameter.getValue());
	    	}
	    	  
	        return "Hi";
	    }
	   
	   @Post
	    public String post() {
	    	Request request= getRequest(); 
	    	
	    	Form form = request.getResourceRef().getQueryAsForm();
	    	for (Parameter parameter : form) {
	    	  System.out.print("parameter " + parameter.getName());
	    	  System.out.println("=" + parameter.getValue());
	    	}
	    	  
	        return "Hi";
	    }

}