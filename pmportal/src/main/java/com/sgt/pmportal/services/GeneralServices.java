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
	 * Logins into a Jira instance
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
	 * Encodes username and password for viewing
	 * 
	 * @param username
	 * @param pass
	 * @return
	 */
    public static String encodeAuth(String username, String pass) {
    	return Base64.getUrlEncoder().encodeToString((username + ":" + pass).getBytes());
    }
    
    /**
     * Converts a BasicIssue of the JRJC to JiraIssue
     * 
     * @param basicIssue
     * @param client
     * @return
     */
	public static JiraIssue toJiraIssue(BasicIssue basicIssue, JiraRestClient client) {
		Promise<Issue> issue = client.getIssueClient().getIssue(basicIssue.getKey());
		Issue realIssue = issue.claim();
		//error handling to prevent null pointer exceptions
		String assigneeName = (realIssue.getAssignee() == null) ? null : 
			realIssue.getAssignee().getDisplayName();
		String description = "";
		String priority="";
		DateTime creationDate = null;
		DateTime dueDate = null;
		try {
			description = realIssue.getDescription();
		} catch(NullPointerException exception){}			

		try{
			priority=realIssue.getPriority().getName();
			
		}catch(NullPointerException exception){
		}
		
		try{
			creationDate=realIssue.getCreationDate();
		} catch(NullPointerException exception){}
		try{
			dueDate=realIssue.getDueDate();
		} catch(NullPointerException exception){}		
		return new JiraIssue (realIssue.getKey(), realIssue.getIssueType().getName(), 
				priority,description, assigneeName, creationDate, dueDate, realIssue.getStatus().getName());
	}
	
	/**
	 * Converts Basic User to JiraUser
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