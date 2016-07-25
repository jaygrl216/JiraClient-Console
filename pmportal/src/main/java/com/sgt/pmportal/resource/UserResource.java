package com.sgt.pmportal.resource;

import java.net.URISyntaxException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.services.GeneralServices;

@Path ("/user/{username}/{password}/{url:.+}")
public class UserResource {

	
	/**
	 * This method will test the login information
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws URISyntaxException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String testLogin(@PathParam ("username") String username, 
			@PathParam ("password")	String password, @PathParam ("url") String url) throws URISyntaxException{
		try{
		@SuppressWarnings("unused")
		JiraRestClient client=GeneralServices.login(url, username, password);
		}catch (Exception e){
			return "Fail";
		}
		return "Success";
	}
}
