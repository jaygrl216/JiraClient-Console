package com.sgt.pmportal.services;

import java.util.ArrayList;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.User;
import com.sgt.pmportal.domain.JiraIssue;
import com.sgt.pmportal.domain.JiraUser;

public class UserServices {
	JiraRestClient client;

	public UserServices(JiraRestClient client) {
		this.client = client;
	}

	public JiraUser getProjectLead(String projectKey){
		User lead = client.getUserClient().getUser(client.getProjectClient().getProject(projectKey).claim().getLead().getName()).claim();
		JiraUser projectLead = toJiraUser(lead);
		return projectLead;
	}

	public JiraUser getAssignee(String issueKey){
		User assignee = client.getUserClient().getUser(client.getIssueClient().getIssue(issueKey).claim().getAssignee().getName()).claim();
		JiraUser issueUser = toJiraUser(assignee);
		return issueUser;
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