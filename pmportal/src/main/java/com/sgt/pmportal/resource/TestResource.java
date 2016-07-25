package com.sgt.pmportal.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path ("/test/{username}/{password}/{url:.+}")
public class TestResource {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String testLogin(){
		return "";
	}
	
}
