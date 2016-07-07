package com.sgt.pmportal.resource;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.ProjectServices;

@Path ("/metrics")
public class MetricResource {
	
	@Path("/project/{projectKey}/{username}/{password}/{url:.+}")
	public String getBasicMetrics(@PathParam ("projectKey") String key, 
			@PathParam ("username") String username, 
			@PathParam ("password") String password, 
			@PathParam("url") String url) throws URISyntaxException{
		JiraRestClient client=GeneralServices.login(url, username, password);
		String responseString="";
		return responseString;
	}

}
