package com.sgt.pmportal.services;

import java.util.ArrayList;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.User;
import com.sgt.pmportal.domain.JiraIssue;
import com.sgt.pmportal.domain.JiraUser;

/**
 * A class that works with Jira Users
 * 
 * @author Jada Washington
 * @author Aman Mital
 *
 */
public class UserServices {
	JiraRestClient client;
	
	/**
	 * Constructor for UserServices
	 * @param client
	 */
	public UserServices(JiraRestClient client) {
		this.client = client;
	}
	
	/**
	 * Gets the lead of the project
	 * 
	 * @param projectKey
	 * @return
	 */
	public JiraUser getProjectLead(String projectKey){
		return toJiraUser(client.getUserClient().getUser
				(client.getProjectClient().getProject(projectKey).claim().getLead().getName()).claim());
	}
	
	/**
	 * Gets who is assigned to the issue
	 * @param issueKey
	 * @return
	 */
	public JiraUser getAssignee(String issueKey){
		return toJiraUser(client.getUserClient().getUser
				(client.getIssueClient().getIssue(issueKey).claim().getAssignee().getName()).claim());
	}
	
	/**
	 * Returns JiraIssues associated with this user
	 * @param name ArrayList<JiraIssue>
	 * @return
	 */
	public ArrayList<JiraIssue> assignedTo(String username) {
		ArrayList<JiraIssue> issues = new ArrayList<JiraIssue>();
		Iterable<BasicIssue> issueList = client.getSearchClient().searchJql(
				"assignee=" + username,1000,0).claim().getIssues();
		System.out.print("Loading Issues for User: " + username + " ...");
		for (BasicIssue i: issueList){
			System.out.print(".");
			JiraIssue j = GeneralServices.toJiraIssue(i, client);
			issues.add(j);
		}
		System.out.println();
		return issues;
	}
	public JiraUser toJiraUser(User user){
		JiraUser jUser=new JiraUser(
				user.getName(), user.getDisplayName(), user.getEmailAddress(), user.getTimezone(), user.getAvatarUri());
		return jUser;
	}

}