package com.sgt.pmportal.domain;

import java.net.URI;

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

}
