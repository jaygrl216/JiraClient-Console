package com.sgt.pmportal.tests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.junit.Test;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.RestClientException;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.MetricsServices;
import com.sgt.pmportal.services.ProjectServices;

public class MetricTest {
	private static final String JIRA_URL = "http://54.152.100.242/jira";
	private static final String JIRA_ADMIN_USERNAME = "amital";
	private static final String JIRA_ADMIN_PASSWORD = "ComPuteR90";
	private static String authorization;
	private static JiraRestClient client=login();
	MetricsServices metricServices=new MetricsServices(client, authorization, JIRA_URL);
	ProjectServices pService=new ProjectServices(client);
	JiraProject project=pService.toJiraProject(client.getProjectClient().getProject("PMPOR").claim(), null);


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
		test.testSEA();

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

		//MetricsService test
		System.out.println("SEA test will display the sea of a project as a\n"
				+ "percentage.\n");
		try{
			Long sea=metricServices.calculateProjectSEA(project);
			System.out.println("The SEA of project "+project.getName()+" is: " + sea + "%");
		} catch (RestClientException noProject){
			System.err.println("Project does not exist");
		}
	}



}
