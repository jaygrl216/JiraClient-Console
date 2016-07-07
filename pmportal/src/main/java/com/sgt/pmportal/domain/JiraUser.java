package com.sgt.pmportal.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * This class represents a user for Jira
 * 
 * @author Aman Mital
 * @author Jada Washington
 * 
 */
public class JiraUser {
	
	protected String userName;
	protected String fullName;
	protected String emailAddress;
	protected String timeZone;
	protected URI avatarURI;
	protected List<JiraIssue> issuesAssigned;
	
	/**
	 * Constructor for a JiraUser
	 * @param uName
	 * @param fName
	 * @param email
	 * @param tZone
	 * @param aURI
	 */
	public JiraUser(String uName, String fName, String email, String tZone, URI aURI){
		userName=uName;
		fullName=fName;
		emailAddress=email;
		timeZone=tZone;
		avatarURI=aURI;
		issuesAssigned = new ArrayList<>();
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public String getTimeZone() {
		return timeZone;
	}
	
	public String JSONString() {
		JSONObject obj = new JSONObject();
		obj.put("name", fullName);
		obj.put("username", userName);
		obj.put("email", emailAddress);
		obj.put("issues", issuesAssigned);
		return obj.toString();
	}

}
