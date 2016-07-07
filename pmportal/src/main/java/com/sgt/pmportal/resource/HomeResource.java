package com.sgt.pmportal.resource;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.ProjectServices;

@Path ("/home/{username}/{password}")
public class HomeResource {
	@GET
	//@Produces(MediaType.APPLICATION_JSON)
	public String getProjects(@PathParam ("username") String username,
			@PathParam ("password") String password) throws URISyntaxException{
		String url="http://54.152.100.242/jira";
		JiraRestClient client=GeneralServices.login(url, username, password);
		String authorization=GeneralServices.encodeAuth(username, password);
		ProjectServices projectService=new ProjectServices(client, authorization, url);
		List<JiraProject> projectList=projectService.getAllJiraProjects();
		return projectList.toString();
	}
}
