package com.sgt.pmportal.resource;

import java.net.URISyntaxException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path ("/user/{username}/{password}/{url:.+}")
public class UserResource {

	
	/**
	 * This method will return the user's information as well as issues they are 
	 * assigned to.
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws URISyntaxException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getProjects(@PathParam ("username") String username, 
			@PathParam ("password")	String password, @PathParam ("url") String url) throws URISyntaxException{
		return "";
	}
}
