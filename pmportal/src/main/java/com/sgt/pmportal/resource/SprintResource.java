package com.sgt.pmportal.resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.domain.JiraIssue;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.ProjectServices;
import com.sgt.pmportal.services.SprintServices;

@Path ("/sprint")
public class SprintResource {
	
	@Path("/backlog/{projectKey}/{username}/{password}/{url:.+}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBasicMetrics(@PathParam ("projectKey") String key, 
			@PathParam ("username") String username, 
			@PathParam ("password") String password, 
			@PathParam("url") String url) throws URISyntaxException, IOException, 
		ParseException{
		
		JiraRestClient client = GeneralServices.login(url, username, password);
		String authorization = GeneralServices.encodeAuth(username, password);
		ProjectServices projectService = new ProjectServices(client, authorization, 
				url);
		SprintServices sprintService = new SprintServices(client, authorization, 
				url);
		
		JiraProject project = projectService.getProjectByKey(key);
		List<JiraIssue> issues = sprintService.inBacklog(project);
		
		StringBuilder responseString = new StringBuilder();
		responseString.append("{\"issues\":");
		JSONArray issueArray = new JSONArray();
		
		for (JiraIssue issue: issues) {
			JSONObject issueObject = new JSONObject(issue.JSONString());
			issueArray.put(issueObject);
		}
		
		responseString.append(issueArray.toString());
		 return responseString.toString();
	
	}

}
