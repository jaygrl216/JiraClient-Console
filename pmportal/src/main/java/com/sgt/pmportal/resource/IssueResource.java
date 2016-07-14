package com.sgt.pmportal.resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import javax.jws.WebService;
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

@Path("/issues/{projectKey}/{username}/{password}/{url:.+}")

/**
 * Gathers issues and basic data for the projects
 * @author Jada
 * @author Aman Mital
 */
@WebService
public class IssueResource {


	@GET
	@Produces(MediaType.APPLICATION_JSON)

	public String getIssue(@PathParam ("username") String username, 
			@PathParam ("password")	String password, @PathParam ("projectKey") String key, 
			@PathParam ("url") String url) throws URISyntaxException, IOException,
			ParseException{
		
		JiraRestClient client = GeneralServices.login(url, username, password);
		String authorization = GeneralServices.encodeAuth(username, password);
		ProjectServices projectService = new ProjectServices(client, authorization, url);
		JiraProject project = projectService.getProjectByKey(key);
		ProjectServices.populateIssues(project);
		
		StringBuilder responseString = new StringBuilder();
		responseString.append("{\"issues\":");
		JSONArray issueArray = new JSONArray();
		for (JiraIssue issue: project.getIssues()){
			JSONObject issueObject = new JSONObject(issue.JSONString());
			issueArray.put(issueObject);
		}

		responseString.append(issueArray.toString());
		responseString.append("}");
		return responseString.toString();
		
		
	}

}
