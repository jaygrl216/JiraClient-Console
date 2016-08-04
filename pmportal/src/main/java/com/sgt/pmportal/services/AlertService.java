package com.sgt.pmportal.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.resource.ConfigResource;

public class AlertService {

	//This could literally take an hour. This method should only be run on a weekly basis, if that
	public static void checkMetrics() throws IOException, URISyntaxException, ParseException{
		JSONObject userObject=new JSONObject(ConfigResource.getAllCredentials());
		JSONArray userArray=userObject.getJSONArray("users");
		for (int i=0; i<userArray.length(); i++){
			JSONObject user=userArray.getJSONObject(i);
			String username=user.getString("username");
			String password=user.getString("password");
			String url=user.getString("url");
			JiraRestClient client=GeneralServices.login(url, username, password);
			String authorization=GeneralServices.encodeAuth(username, password);
			ProjectServices projectService=new ProjectServices(client, authorization, url);
			List<JiraProject> projectList=projectService.getAllJiraProjects();
			MetricsServices metricService=new MetricsServices(client, authorization, url);
			for (JiraProject project:projectList){
				String key=project.getKey();
				Double sea=metricService.calculateProjectSEA(project, null);
				Double eea=metricService.calculateProjectEEA(project, null);
				Long bugs=metricService.calculateBugs(key);
	
			}
		}
	}
	//This method sends an alert notification
	public static void sendMail(String m_to, String body){
		NotificationService nService=new NotificationService(m_to, "PM-Portal Alert", body);
	}
}
