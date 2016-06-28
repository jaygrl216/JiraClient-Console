package com.sgt.pmportal.tests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.RestClientException;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.Sprint;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.MetricsServices;
import com.sgt.pmportal.services.ProjectServices;
import com.sgt.pmportal.services.SprintServices;

public class MetricTest {
	private static final String JIRA_URL = "http://54.152.100.242/jira";
	private static final String JIRA_ADMIN_USERNAME = "admin";
	private static final String JIRA_ADMIN_PASSWORD = "admin";
	private static String authorization;
	private static JiraRestClient client=login();
	MetricsServices metricServices=new MetricsServices(client, authorization, JIRA_URL);
	ProjectServices pService=new ProjectServices(client);
	SprintServices sprintService=new SprintServices(client, authorization, JIRA_URL);
	JiraProject project=pService.toJiraProjectWithIssues(client.getProjectClient().getProject("PMPOR").claim());
	ArrayList<Sprint> sprintList=new ArrayList<Sprint>();

	/**
	 * logins into JiraClient
	 * @return JiraRestClient
	 */
	private static JiraRestClient login() {
		client = null;
		try {
			client = GeneralServices.login(JIRA_URL, JIRA_ADMIN_USERNAME, 
					JIRA_ADMIN_PASSWORD);
			authorization=GeneralServices.encodeAuth(JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);
		} catch (URISyntaxException e) {
			fail("Cannot login");
		}
		return client;
	}

	public static void main(String[] args) throws IOException, ParseException{
		MetricTest test=new MetricTest();
		//this runs a series of tests as a java application
		test.printInfo();
		//test.testProgress();
		//test.testSEA();
		test.testOverallSEA();
		System.out.println("Finished");

	}

	@Test
	public void printInfo() {
		System.out.println("******PM-Portal Metric test********\n");
		System.out.println("Jira server: " + JIRA_URL);
		System.out.println("Using login for: " + JIRA_ADMIN_USERNAME+"\n");
	}
	public void testProgress(){

		//MetricsService test
		System.out.println("Progress test will display the progress of a project as a\n"
				+ "percentage.\n");
		try{
			String progress=metricServices.calculateProgress(project.getKey());
			System.out.println("The progress on project "+project.getName()+" is: " + progress);
		} catch (RestClientException noProject){
			System.err.println("Project does not exist");
		}
	}

	public void testSEA() throws IOException, ParseException{
		System.out.println("Getting sprints...");
		try{
		sprintList=sprintService.getClosedSprintsByProject(project);
		Sprint sprint=sprintList.get(0);
		//MetricsService test
		System.out.println("SEA test will display the sea of a sprint as a\n"
				+ "percentage.\n");
			double sea=metricServices.calculateSprintSEA(sprint);
			System.out.println("The SEA of sprint "+sprint.getName()+" is: " + sea + "%");
	}catch(NullPointerException noSprint){
		System.err.println(noSprint);
	}
	}
	public void testOverallSEA() throws IOException, ParseException{
		System.out.println("Overall SEA test will display the SEA of a project and its standard deviation");
		ArrayList<Double> seaMetric=metricServices.calculateProjectSEA(project);
		System.out.println("SEA: "+ seaMetric.get(0) + "+/- " + seaMetric.get(1)+"%");
	}

}
