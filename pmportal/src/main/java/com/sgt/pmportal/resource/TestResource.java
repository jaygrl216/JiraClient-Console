package com.sgt.pmportal.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.services.GeneralServices;

@Path ("/test/{username}/{password}/{url:.+}")
public class TestResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String testLogin(@PathParam ("username") String username, 
			@PathParam ("password")	String password, 
			@PathParam ("url") String url){
		try{
			@SuppressWarnings("unused")
			JiraRestClient client=GeneralServices.login(url, username, password);
		}catch (Exception e){
			return "Failed";
		}
		return "Success";
	}
	
}
