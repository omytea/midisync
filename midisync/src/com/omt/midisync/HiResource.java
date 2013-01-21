package com.omt.midisync;

//import java.io.IOException;
//import javax.servlet.http.*;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
//import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
//import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Resource which has only one representation.
 * 
 */
public class HiResource extends ServerResource {

	@Get
	public String represent() {

		System.out.print("HiResource  ");

		Request request = getRequest();

		Form form = request.getResourceRef().getQueryAsForm();
		for (Parameter parameter : form) {
			System.out.print("parameter " + parameter.getName());
			System.out.println("/" + parameter.getValue());
		}

		return "HiResource";
	}

	@Put
	public String put() {

		System.out.print("HiResource  ");

		Request request = getRequest();

		Form form = request.getResourceRef().getQueryAsForm();
		for (Parameter parameter : form) {
			System.out.print("parameter " + parameter.getName());
			System.out.println("/" + parameter.getValue());
		}

		return "HiResource";
	}

}