package com.sgt.pmportal.resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.ProjectServices;
import com.sgt.pmportal.services.SprintServices;

@Path ("/user/{key}/{username}/{password}/{url:.+}")
public class UserResource {

	
	/**
	 * This method will return the resource allocation of each user
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws URISyntaxException
	 * @throws ParseException 
	 * @throws IOException 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getResourceAllocation(@PathParam ("key") String projectKey, @PathParam ("username") String username, 
			@PathParam ("password")	String password, @PathParam ("url") String url) throws URISyntaxException, IOException, ParseException{
		JiraRestClient client=GeneralServices.login(url, username, password);
		String authorization=GeneralServices.encodeAuth(username, password);
		ProjectServices projectService=new ProjectServices(client, authorization, url);
		SprintServices sprintService=new SprintServices(client, authorization, url);
		JiraProject project=projectService.getProjectByKey(projectKey);
		JSONObject returnObject=sprintService.resourceAllocation(project);
		if (returnObject==null){
			String response="{\"users\":[{\"name\":\"Unassigned\", \"effort\":0, \"numIssues\":0}]";
			return response;
		};
		return returnObject.toString();
	}
}
