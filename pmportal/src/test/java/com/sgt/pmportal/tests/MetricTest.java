package com.sgt.pmportal.tests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
	private static final String JIRA_ADMIN_USERNAME = "amital";
	private static final String JIRA_ADMIN_PASSWORD = "ComPuteR90";
	private static String authorization;
	private static JiraRestClient client=login();
	MetricsServices metricServices=new MetricsServices(client, authorization, JIRA_URL);
	ProjectServices pService=new ProjectServices(client, authorization, JIRA_URL);
	SprintServices sprintService=new SprintServices(client, authorization, JIRA_URL);
	JiraProject project=pService.toJiraProject(client.getProjectClient().getProject("PA").claim(),null);
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
		//test.testSprintSEA();
		//test.testOverallSEA();

		test.testSprintEEA();
		test.testOverallEEA();
		//test.testAllDefects();
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
			double progress=metricServices.calculateProgress(project.getKey());
			System.out.println("The progress on project "+project.getName()+" is: " + progress+"%");
		} catch (RestClientException noProject){
			System.err.println("Project does not exist");
		}
	}

	public void testSprintSEA() throws IOException, ParseException{
		System.out.println("Getting sprints...");
		try{
		sprintList=sprintService.getClosedSprintsByProject(project);
		Sprint sprint=sprintList.get(0);
		//MetricsService test
		System.out.println("SEA test will display the sea of a sprint as a\n"
				+ "percentage.\n");
			double sea=MetricsServices.calculateSprintSEA(sprint);
			System.out.println("The SEA of sprint "+sprint.getName()+" is: " + sea+"\n");
	}catch(NullPointerException noSprint){
		System.err.println(noSprint);
	}
	}
	public void testOverallSEA() throws IOException, ParseException{
		System.out.println("Overall SEA test will display the SEA of a project and its standard deviation");
		ArrayList<Double> seaMetric=metricServices.calculateProjectSEA(project);
		System.out.println("SEA: "+ seaMetric.get(0) + "+/- " + seaMetric.get(1)+"\n");
	}

public void testSprintEEA() throws IOException, ParseException{
	System.out.println("Sprint EEA test will display the EEA of a sprint");
	try{
	sprintList=sprintService.getClosedSprintsByProject(project);
	Sprint sprint=sprintList.get(0);
	System.out.println("EEA: "+ metricServices.calculateSprintEEA(sprint)+"\n");
	}catch(NullPointerException noSprint){
		System.err.println(noSprint);
	}
}

public void testOverallEEA() throws IOException, ParseException{
	System.out.println("Overall EEA test will display the EEA of a project and its standard deviation");
	ArrayList<Double> eeaMetric=metricServices.calculateProjectEEA(project);
	System.out.println("EEA: "+ eeaMetric.get(0) + "+/- " + eeaMetric.get(1)+"\n");
}


public void testAllDefects() throws IOException, ParseException{
	System.out.println("All defects test will calculate all metrics and find defects for all projects");
	List<Long> defectArray=metricServices.calculateDefectTotal();
	System.out.println("Defects");
	System.out.println("Bugs: "+defectArray.get(0));
	System.out.println("SEA Warnings: "+defectArray.get(1));
	System.out.println("EEA Warnings: "+defectArray.get(2));
	System.out.println("Overdue projects: "+defectArray.get(3)+"\n");
}
}
