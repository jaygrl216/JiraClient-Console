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

public class ScheduleService {

	//This could literally take an hour. This method should only be run on a weekly basis, if that
	public static void checkMetrics() throws IOException, URISyntaxException, ParseException{
		JSONArray userArray=getCredentials().getJSONArray("users");
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
				String name=project.getName();
				Double sea=metricService.calculateProjectSEA(project, null);
				Double eea=metricService.calculateProjectEEA(project, null);
				Long bugs=metricService.calculateBugs(key);
				Double progress=metricService.calculateProgress(key);			
			}
		}
	}
	//this is a clone of the method found in ConfigResource.java
	public static JSONObject getCredentials() throws IOException{
		JSONObject responseObject=new JSONObject();
		JSONArray responseArray=new JSONArray();
		try{
			//Tomcat
			//read file
			String fileString=new String(Files.readAllBytes(Paths.get("webapps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			//convert to JSON so it can be easily manipulated client-side
			String[] userArray=fileString.split(";");

			for (String user:userArray){
				String[] userData=user.split(",");
				if (userData.length>1){
					JSONObject tempObject=new JSONObject();
					tempObject.put("username", userData[0]);
					tempObject.put("email", userData[2]);
					tempObject.put("url", userData[3]);
					responseArray.put(tempObject);
				}
			}
		}catch (Exception e){
			//glassfish
			String fileString=new String(Files.readAllBytes(Paths.get("../applications/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			String[] userArray=fileString.split(";");

			for (String user:userArray){
				String[] userData=user.split(",");
				if (userData.length>1){
					JSONObject tempObject=new JSONObject();
					tempObject.put("username", userData[0]);
					tempObject.put("email", userData[2]);
					tempObject.put("url", userData[3]);
					responseArray.put(tempObject);
				}
			}
		}
		responseObject.put("users", responseArray);
		return responseObject;
	}
	//This method sends an alert notification
	public static void sendMail(String m_to, String body){
		NotificationService nService=new NotificationService(m_to, "PM-Portal Alert", body);
	}
}
