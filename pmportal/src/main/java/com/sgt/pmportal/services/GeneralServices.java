/**
 * PM-Portal 
 * 
 * 
 * @author Jada Washington
 * @author Aman Mital
 * @version 1.1
 * @since 6/17/2016
 */

package com.sgt.pmportal.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.sgt.pmportal.domain.JiraIssue;
import com.sgt.pmportal.domain.JiraUser;

public class GeneralServices {

	/**
	 * Logs in to a Jira instance
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 * @throws URISyntaxException
	 */
	public static JiraRestClient login (String url, String username, String password) 
			throws URISyntaxException {
		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		URI uri = new URI(url);
		return factory.createWithBasicHttpAuthentication(uri, username, password);
	}

	/**
	 * Encodes username and password as a Base64 String (used for requests independent of the JRJC)
	 * 
	 * @param username
	 * @param pass
	 * @return String
	 */
	public static String encodeAuth(String username, String pass) {
		return Base64.getUrlEncoder().encodeToString((username + ":" + pass).getBytes());
	}

	/**
	 * Converts a BasicIssue of the JRJC to a JiraIssue
	 * 
	 * @param basicIssue
	 * @param client
	 * @return JiraIssue
	 */
	public static JiraIssue toJiraIssue(BasicIssue basicIssue, JiraRestClient client) {
		Promise<Issue> issue = client.getIssueClient().getIssue(basicIssue.getKey());
		Issue realIssue = issue.claim();
		//error handling to prevent null pointer exceptions
		String assigneeName = (realIssue.getAssignee() == null) ? null : 
			realIssue.getAssignee().getDisplayName();
		String description = realIssue.getDescription();
		String priority = (realIssue.getPriority() == null) ? null : realIssue.getPriority().getName();
		DateTime creationDate = realIssue.getCreationDate();
		DateTime dueDate = realIssue.getDueDate();
		String status = (realIssue.getStatus() == null) ? null : realIssue.getStatus().getName();
		return new JiraIssue (realIssue.getKey(), realIssue.getIssueType().getName(), 
				priority,description, assigneeName, creationDate, dueDate, status);
	}

	/**
	 * Converts a BasicUser of the JRJC to a JiraUser
	 * @param user
	 * @param client
	 * @return JiraUser
	 */
	public static JiraUser toJiraUser(BasicUser user, JiraRestClient client) {
		Promise<User> jiraUser = client.getUserClient().getUser(user.getName());
		User realJiraUser = jiraUser.claim();

		return new JiraUser(realJiraUser.getName(), realJiraUser.getDisplayName(), 
				realJiraUser.getEmailAddress(), realJiraUser.getTimezone(), 
				realJiraUser.getAvatarUri());
	}

}