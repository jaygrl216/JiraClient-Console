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
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.MetricsServices;
import com.sgt.pmportal.services.ProjectServices;


@Path ("/home/{username}/{password}/{url:.+}")

/**
 * This class contains a REST method that displays information about a
 * Jira instance
 *
 * @author Aman Mital
 * @author Jada Washington
 *
 */
public class HomeResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getProjects(@PathParam ("username") String username, 
			@PathParam ("password")	String password, @PathParam ("url") String url)
					throws URISyntaxException, IOException, ParseException {

		JiraRestClient client = GeneralServices.login(url, username, password);
		String authorization = GeneralServices.encodeAuth(username, password);
		ProjectServices projectService = new ProjectServices(client,
				authorization, url);
		MetricsServices metricService = new MetricsServices(client,
				authorization, url);
		List<JiraProject> projectList = projectService.getAllJiraProjects();

		StringBuilder responseString = new StringBuilder();
		responseString.append("{\"projects\":");
		JSONArray projectArray = new JSONArray();
		for (JiraProject project:projectList){
			if(metricService.calculateProgress(project.getKey()) >= 100) {
				project.setCompleted(true);
			}
			JSONObject projectObject=new JSONObject(project.JSONString());
			projectArray.put(projectObject);
		}
		responseString.append(projectArray.toString());
		responseString.append("}");
		return responseString.toString();
	}

}

