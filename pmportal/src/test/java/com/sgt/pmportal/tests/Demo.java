package com.sgt.pmportal.tests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.JiraUser;
import com.sgt.pmportal.domain.Sprint;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.MetricsServices;
import com.sgt.pmportal.services.ProjectServices;
import com.sgt.pmportal.services.SprintServices;
import com.sgt.pmportal.services.UserServices;

public class Demo {
	private static final String JIRA_URL = "http://54.152.100.242/jira";
	private static final String JIRA_ADMIN_USERNAME = "amital";
	private static final String JIRA_ADMIN_PASSWORD = "ComPuteR90";
	private static String authorization=GeneralServices.encodeAuth(JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);
	private static JiraRestClient client = login();
	/**
	 * logins into JiraClient
	 * @return JiraRestClient
	 */
	private static JiraRestClient login() {
		JiraRestClient client = null;
		try {
			client = GeneralServices.login(JIRA_URL, JIRA_ADMIN_USERNAME, 
					JIRA_ADMIN_PASSWORD);

		} catch (URISyntaxException e) {
			fail("Cannot login");
		}
		return client;

	}
	public static void main(String[] args) throws IOException, ParseException {
		Demo demo = new Demo();
		demo.printInfo();
		demo.projectDemo();
		demo.userDemo();
		demo.sprintDemo();
		demo.metricDemo();
	}
	public void printInfo() {
		System.out.println("*******PM-Portal Demonstration********");
		System.out.println("**************************************");
		System.out.println("******Aman Mital/Jada Washington******\n");
		System.out.println("Jira server: " + JIRA_URL);
		System.out.println("Using login for: " + JIRA_ADMIN_USERNAME+"\n");
	}
	public void projectDemo(){
		System.out.println("Project Services Demo");
		System.out.println("---------------------\n");
		ProjectServices projectService = new ProjectServices(client, authorization, JIRA_URL);
		
		List<JiraProject> projects = projectService.getAllJiraProjects();
		System.out.format("This Jira instance has %d projects associated with it.\n", projects.size());
		
		System.out.println("Now attemping to access this Project through Jira.\n");
		JiraProject portal = projectService.getProjectByKey("PMPOR");
		
		if(portal != null) {
			System.out.println("Project was found.......");
			System.out.println(portal.toString());
		} else {
			System.out.println("Could not access with provided key");
		}
		
		System.out.format("This project is due on %s\n", portal.getDueDate().toString());
		try {
			System.out.format("Based on the velocity of this project, this project is estimated to"
					+ " be done %s.\n", projectService.projectedDueDate(portal).toString());
		} catch (IOException e) {
			System.err.println("Error with Input/Output");
		}
		
		ProjectServices.populateIssues(portal);
		
		System.out.format("There are %d issues associated with this project\n\n", portal.getNumIssues());
	

	}
	public void userDemo(){
		System.out.println("User Services Demo");
		System.out.println("------------------\n");
		
		System.out.println("Attempting to get project lead for Radar Analytics");
		JiraUser user = UserServices.getProjectLead("RA", client);
		
		if(user != null) {
			System.out.println("Found a lead for this project");
			System.out.format("The lead for Radar Analytics is %s.\n\n", user.getFullName());
		} else {
			System.out.println("Either project does not exist or there is no lead");
		}
	}
	public void sprintDemo() throws IOException, ParseException{
		System.out.println("Sprint Services Demo");
		System.out.println("--------------------\n");
		SprintServices sprintService=new SprintServices(client, authorization, JIRA_URL);
		ProjectServices projectService=new ProjectServices(client, authorization, JIRA_URL);
		JiraProject project=projectService.getProjectByKey("DEV");
		List<Sprint> sprintList=sprintService.getClosedSprintsByProject(project);
		System.out.println("Closed sprints for project '" + project.getName() + "':");
		for (Sprint sprint:sprintList){
			System.out.println(sprint.getName() + ", ID: " + sprint.getId() + ", Board ID: " + sprint.getBoardId() + ", State: " + sprint.getState());
		}
	}
	public void metricDemo() throws IOException, ParseException{
		System.out.println("Metric Services Demo");
		System.out.println("--------------------\n");
		MetricsServices metricService=new MetricsServices(client, authorization, JIRA_URL);
		ProjectServices projectService=new ProjectServices(client, authorization, JIRA_URL);
		JiraProject project=projectService.getProjectByKey("DEV");
		System.out.println("This method will display schedule estimation accuracy (SEA),");
				System.out.println("effort estimation accuracy (EEA), and bug count per sprint with predicted next values");
		List<List<Double>> dataList=metricService.predictNext(project);
		if (dataList.size()>0){
			List<Double >seaList=dataList.get(0);
			List<Double> eeaList=dataList.get(1);
			List<Double> bugList=dataList.get(2);
			double seaSlope=metricService.getRegressionSlope(seaList);
			double eeaSlope=metricService.getRegressionSlope(eeaList);
			double bugSlope=metricService.getRegressionSlope(bugList);
			List<Double> seaForecast=metricService.getForecastInterval(seaList, seaSlope);
			List<Double> eeaForecast=metricService.getForecastInterval(eeaList, eeaSlope);
			List<Double> bugForecast=metricService.getForecastInterval(bugList, bugSlope);
			System.out.println("SEA values:");
			for (int i=0; i<seaList.size(); i++){
				System.out.print("\nSprint " +(i+1)+", SEA: "+seaList.get(i));
			}
			System.out.print("+- "+seaForecast.get(0)+" <--- Predicted value with forecast interval\n");
			System.out.println("Error on regression: " + seaForecast.get(1)+"\n\nEEA values:");
			for (int i=0; i<eeaList.size(); i++){
				System.out.print("\nSprint " +(i+1)+", EEA: "+eeaList.get(i));
			}
			System.out.print("+- "+eeaForecast.get(0)+ " <--- Predicted value with forecast interval\n");
			System.out.println("Error on regression: "+eeaForecast.get(1)+"\n\nBug values:");
			for (int i=0; i<bugList.size(); i++){
				System.out.print("\nSprint " +(i+1)+", Bugs: "+bugList.get(i));
			}
			System.out.print("+- "+bugForecast.get(0)+ " <--- Predicted value with forecast interval\n");
			System.out.println("Error on regression: "+bugForecast.get(1));
		} else{
			System.err.println("No data available!");
		}
	}
}
