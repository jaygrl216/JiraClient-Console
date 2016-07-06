package com.sgt.pmportal.tests;
import static org.junit.Assert.fail;

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
public class Demo {
	private static final String JIRA_URL = "http://54.152.100.242/jira";
	private static final String JIRA_ADMIN_USERNAME = "amital";
	private static final String JIRA_ADMIN_PASSWORD = "ComPuteR90";
	private static String authorization;
	private static JiraRestClient client=login();
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
	public static void main(String[] args) {
		Demo demo=new Demo();
		demo.printInfo();
		demo.projectDemo();
		demo.userDemo();
		demo.sprintDemo();
		demo.metricDemo();
	}
	public void printInfo() {
		System.out.println("*******PM-Portal Demonstration********\n");
		System.out.println("**************************************\n");
		System.out.println("******Aman Mital/Jada Washington******\n");
		System.out.println("Jira server: " + JIRA_URL);
		System.out.println("Using login for: " + JIRA_ADMIN_USERNAME+"\n");
	}
	public void projectDemo(){
		System.out.println("Project Services Demo");
		System.out.println("---------------------\n");
		ProjectServices projectService=new ProjectServices(client, authorization, JIRA_URL);

	}
	public void userDemo(){
		System.out.println("User Services Demo");
		System.out.println("------------------\n");
	}
	public void sprintDemo(){
		System.out.println("Sprint Services Demo");
		System.out.println("--------------------\n");
		SprintServices sprintService=new SprintServices(client, authorization, JIRA_URL);
	}
	public void metricDemo(){
		System.out.println("Sprint Services Demo");
		System.out.println("--------------------\n");
		MetricsServices metricService=new MetricsServices(client, authorization, JIRA_URL);
	}
}