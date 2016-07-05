package com.sgt.pmportal.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.json.JSONException;
import org.junit.Test;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.Project;
import com.sgt.pmportal.domain.JiraIssue;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.JiraUser;
import com.sgt.pmportal.domain.Sprint;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.MetricsServices;
import com.sgt.pmportal.services.ProjectServices;
import com.sgt.pmportal.services.SprintServices;
import com.sgt.pmportal.services.UserServices;

public class Tests {

	private static final String JIRA_URL = "http://52.204.130.7:8081/";
	private static final String JIRA_ADMIN_USERNAME = "JWashington@sgt-inc.com";
	private static final String JIRA_ADMIN_PASSWORD = "Diamond2017";

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


	public static void main(String[] args) throws IOException, JSONException, ParseException{
		Tests test = new Tests();
		//this runs a series of tests as a java application

			test.printInfo();
			test.testProjectInfo();
		//		test.versionTest();
		//		test.metricsServiceTest();
		//		test.userServicesTest();
		//		test.sprintServiceTest();
		//test.projectAnalysisTest();
		System.out.println("Finished");

	}

	@Test
	public void printInfo() {
		System.out.println("******PM-Portal Service layer test********\n");
		System.out.println("Jira server: " + JIRA_URL);
		System.out.println("Using login for: " + JIRA_ADMIN_USERNAME+"\n");
	}


	@Test
	public void testProjectInfo() {
		System.out.println("Project Services Test");
		JiraRestClient client = login();
		ProjectServices pservices = new ProjectServices(client, JIRA_ADMIN_PASSWORD, JIRA_URL);
		int numProjects = pservices.getAllJiraProjects().size();
		
		assertEquals(2, numProjects);
		
		System.out.format("This Jira instance has %s project(s) associated with it.\n", numProjects);
		
		JiraProject testProject = pservices.getProjectByKey("TP");
		assertTrue(testProject != null);
		
		System.out.format("We will be working with the the project: %s(%s) for this test.\n", 
				testProject.getName(), testProject.getKey());


		try {
			JiraProject pm = pservices.getProjectByName("PM-Portal");
			ProjectServices.populateIssues(pm);
			System.out.format("Gathering information for project: %s\n\n", 
					pm.getName());
			System.out.format("There are %d issues for %s.\n\n", 
					pm.getNumIssues(), pm.getName());

			System.out.println("Printing issues...");
			for(JiraIssue i: pm.getIssues()) {
				System.out.println(i.toString());
			}
			System.out.println("All issues printed.");
		} catch (NullPointerException e) {
			System.err.println("No project was found");
		}

		//			try{
		//
		//				Iterable<BasicIssue> basic = client.getSearchClient().searchJql(
		//						"project = "+ pm.getKey()).claim().getIssues();
		//				System.out.println("Retrieving issues from project \"" + pm.getName()+"\"");
		//				for(BasicIssue b: basic) {
		//					Promise<Issue> issue = client.getIssueClient().getIssue(b.getKey());
		//					Issue realIssue = issue.claim();
		//					System.out.println((char)27+"[32m"+realIssue.getKey()+": "+realIssue.getIssueType().getName()+(char)27+"[0m");
		//				}
		//			} catch(NullPointerException noProject){
		//				System.err.println("Project does not exist");
		//			}
		System.out.println("");
	}
	@Test
	public void versionTest(){
		System.out.println("Release test");
		JiraRestClient client=login();
		ProjectServices pservice=new ProjectServices(client, JIRA_ADMIN_PASSWORD, JIRA_URL);
		try{
			JiraProject project=pservice.getProjectByKey("PA");
			//ReleaseService test
			System.out.println("Version test will display project version (0 if no released versions).\n");
			System.out.println("Current release for project \""+ project.getName()+"\":");
			System.out.println("Version: "+project.getNumRelease());
			System.out.println("");
		}catch (RestClientException|NullPointerException noProject){
			System.err.println("Project does not exist");
		}
	}
	@Test
	public void metricsServiceTest(){
		System.out.println("Metric Services Test");
		String authorization=Base64.getUrlEncoder().encodeToString((JIRA_ADMIN_USERNAME + ":" + JIRA_ADMIN_PASSWORD).getBytes());
		JiraRestClient client=login();
		//MetricsService test
		System.out.println("Metrics Services test will display the progress of a project as a\n"
				+ "percentage.\n");
		try{
			MetricsServices metricServices=new MetricsServices(client, authorization, JIRA_URL);
			Project project=client.getProjectClient().getProject("PA").claim();
			double progress=metricServices.calculateProgress(project.getKey());
			System.out.println("The progress on project "+project.getName()+" is:" +progress+"%");
		} catch (RestClientException noProject){
			System.err.println("Project does not exist");
		}
	}
	@Test
	public void userServicesTest(){
		JiraRestClient client=login();

		//UserServices test
		System.out.println("User Services test will first: retrieve the assignee to an issue and retrieve their other issues\n"
				+ "and second: retrieve the lead of a project\n");
		UserServices userService=new UserServices(client);
		String issueKey="PA-1";
		try{
			JiraUser assignee=userService.getAssignee(issueKey);
			System.out.println("The user assigned to issue " + issueKey + " is:");
			System.out.println("User name:" + assignee.getUserName());
			System.out.println("Display name:" +assignee.getFullName());
			System.out.println("Email:"+assignee.getEmailAddress());
			System.out.println("Time zone:" +assignee.getTimeZone());

			System.out.println();
			for (JiraIssue j: userService.assignedTo(assignee.getUserName())) {
				System.out.println(j);
			}

		} catch (NullPointerException nException){
			System.err.println("No one assigned to issue\n");
		}

		System.out.print("\n");
		try{
			String projectKey="PA";
			System.out.println("The project lead of \"" + projectKey + "\" is:");
			JiraUser lead=userService.getProjectLead(projectKey);
			System.out.println("User name: " +lead.getUserName());
			System.out.println("Display name:"+lead.getFullName());
			System.out.println("Email:"+lead.getEmailAddress());
			System.out.println("Time zone:" +lead.getTimeZone());
		} catch(RestClientException noProject){
			System.err.println("Project does not exist");
		}
	}

	@Test
	public void sprintServiceTest() throws IOException, JSONException, ParseException{
		String authorization=Base64.getUrlEncoder().encodeToString((JIRA_ADMIN_USERNAME + ":" + JIRA_ADMIN_PASSWORD).getBytes());
		JiraRestClient client=login();
		ProjectServices pService=new ProjectServices(client, JIRA_ADMIN_PASSWORD, JIRA_URL);
		System.out.println("Sprint Services test. First, it will retrieve all open sprints from a project,\n"
				+ "then closed sprints, and finally future sprints. Then all issues for one of these \nsprints will be displayed.\n");
		JiraProject project=pService.getProjectByKey("PA");
		SprintServices sprintService=new SprintServices(client, authorization, JIRA_URL);
		Sprint sprint = null;
		//open
		System.out.println("Open sprints for project \"" +project.getName()+"\":");

		List<Sprint> sprintActiveList=sprintService.getOpenSprintsByProject(project);
		for (Sprint sl:sprintActiveList){
			System.out.println(sl.getName());
			System.out.println(sl.getState()+"\n");
		}
		if (Objects.equals(sprintActiveList.size(), 0)){
			System.err.println("No open sprints.");
		}
		System.out.println("");
		//closed
		System.out.println("Closed sprints for project \"" +project.getName()+"\":");
		List<Sprint> sprintClosedList=sprintService.getClosedSprintsByProject(project);
		for (Sprint sl:sprintClosedList){
			System.out.println(sl.getName());
			System.out.println(sl.getState()+"\n");
		} 
		//store one of the closed sprints as our test sprint
		sprint=sprintClosedList.get(0);
		if (Objects.equals(sprintClosedList.size(), 0)){
			System.err.println("No closed sprints.");
		}
		System.out.println("");
		//future
		System.out.println("Future sprints for project \"" + project.getName() + "\":");
		ArrayList<Sprint> sprintFutureList=sprintService.getFutureSprintsByProject(project);
		for (Sprint sl:sprintFutureList){
			System.out.println(sl.getName());
			System.out.println(sl.getState()+"\n");
		}
		if (Objects.equals(sprintFutureList.size(), 0)){
			System.err.println("No future sprints.");
		}


		System.out.println("");
		//get issues from sprint
		if (sprint !=null){
			List<JiraIssue> issueList=sprintService.getIssuesBySprint(sprint, client);
			System.out.println("\nIssues in sprint \"" +sprint.getName()+"\":\n");
			for (JiraIssue il:issueList){
				System.out.println(il.getKey());

				System.out.println("");
				//get issues from sprint
				if (sprint !=null){
					issueList=sprintService.getIssuesBySprint(sprint, client);
					System.out.println("\nIssues in sprint \"" +sprint.getName()+"\":\n");
					for (JiraIssue i:issueList){
						System.out.println(i.getKey());
					}

				}
			}
			System.out.println("");
		}
	}

	@Test
	public void projectAnalysisTest() throws IOException, ParseException{
		System.out.println("Project Analysis Test");
		JiraRestClient client = login();
		ProjectServices pService = new ProjectServices(client, 
				GeneralServices.encodeAuth(JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD), 
				JIRA_URL);
		SprintServices sprintServ = new SprintServices(client, 
				GeneralServices.encodeAuth(JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD), 
				JIRA_URL);

		JiraProject project = pService.getProjectByKey("TP");
		int closed = sprintServ.getClosedSprintsByProject(project).size();
		int open = sprintServ.getOpenSprintsByProject(project).size();
		int future = sprintServ.getFutureSprintsByProject(project).size();
		
		assertEquals(0, closed);
		assertEquals(1, open);
		assertEquals(0, future);
		
		
//		JiraProject project = pService.getProjectByKey("PMPOR");
//		int closed = sprintServ.getClosedSprintsByProject(project).size();
//		int open = sprintServ.getOpenSprintsByProject(project).size();
//		int future = sprintServ.getFutureSprintsByProject(project).size();
//
//		System.out.format("Closed Sprints for %s: %d\n", project.getKey(), closed);
//		System.out.format("Open Sprints for %s: %d\n", project.getKey(), open);
//		System.out.format("Future Sprints for %s: %d\n", project.getKey(), future);
//
//				sprintServ.getAllSprintsForProject(project);
//				Date dueDate = project.getDueDate();
//				Date projectedDate = pService.projectedDueDate(project);
//		
//				System.out.println("The project is supposed to be completed on " 
//						+ dueDate.toString());
//				System.out.println("The project is expected to be completed on " 
//						+ projectedDate.toString());

	}
}
