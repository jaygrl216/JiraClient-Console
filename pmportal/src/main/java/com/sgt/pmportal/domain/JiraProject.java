package com.sgt.pmportal.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.atlassian.jira.rest.client.domain.BasicUser;

/**
 * This class represents a project from Jira. It contains information about the 
 * project such as the name, key, lead, description, releases, issues and the URI.
 * It also shows whether or not the project is completed and if its overdue.
 * 
 * @author Jada Washington
 * @author Aman Mital
 *
 */
public class JiraProject {
	protected String name;
	protected String key;
	protected BasicUser lead;
	protected String description;
	protected List<Release> releases;
	protected URI uri;
	protected List<JiraIssue> issues;
	protected Date due;
	protected boolean isComplete;
	protected boolean isOverdue;
	
	/**
	 * Constructor for the JiraProject
	 * 
	 * @param name
	 * @param key
	 * @param lead
	 * @param description
	 * @param releases
	 * @param uri
	 */
	public JiraProject(String name, String key, BasicUser lead, String description, 
			List<Release> releases, URI uri) {
		this.name = name;
		this.key = key;
		this.lead = lead;
		this.description = description;
		this.releases = new ArrayList<>(releases);
		this.uri = uri;
		isComplete = false;
		isOverdue = false;
		issues = new ArrayList<>();
		due = new Date(0);
	}
	
	/**
	 * Constructor for JiraProject with given issueList
	 * 
	 * @param name
	 * @param key
	 * @param lead
	 * @param description
	 * @param versionsToRelease
	 * @param uri
	 * @param issueList
	 */
	public JiraProject(String name, String key, BasicUser lead, String description,
			List<Release> versionsToRelease, URI uri, List<JiraIssue> issueList) {
		this.name = name;
		this.key = key;
		this.lead = lead;
		this.description = description;
		this.releases = new ArrayList<>(releases);
		this.uri = uri;
		isComplete = false;
		isOverdue = false;
		issues = new ArrayList<>();
		due = setDefaultDueDate();
	}
	
	public String getName() {
		return name;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getLead() {
		return lead.getDisplayName();
	}
	
	/**
	 * The description of the project
	 * 
	 * @return String
	 */
	public String description() {
		return description;
	}
	
	/**
	 * The URI of the project
	 * @return URI
	 */
	public URI getURI() {
		return uri;
	}
	
	public void setCompleted (boolean b) {
		isComplete = b;
	}
	
	/**
	 * completes the project
	 */
	public void complete() {
		isComplete = true;
	}
	
	public int getNumRelease() {
		return releases.size();
	}
	
	public Date getDueDate() {
		return due;
	}
	
	private Date setDefaultDueDate() {
		Date latest = new Date(0);
		
		for (JiraIssue i: issues) {
			Date issueDate = i.getDueDate().toDate();
			if (issueDate.compareTo(latest) > 0) {
				latest = issueDate;
			}
		}
		
		return latest;
	}
	
	public void setDueDate(Date d) {
		due = d;
	}
	
	/**
	 * Adds a JiraIssue to the list of issues for the project
	 * @param jiraIssue
	 */
	public void addToIssues(JiraIssue jiraIssue) {
		issues.add(jiraIssue);
		
	}
	
	public int getNumIssues() {
		return issues.size();
	}
	
	public List<JiraIssue> getIssues() {
		return issues;
	}
	
	public List<Release> getReleases() {
		return releases;
	}
	
	public boolean getIsOverdue() {
		return isOverdue;
	}
	
	/**
	 * Changes and returns isOverdue based on if date is past due date
	 * 
	 * @return boolean
	 */
	public boolean seeIfOverdue() {
		Date today = Calendar.getInstance().getTime();
		if (today.compareTo(due) > 0) {
			isOverdue = true;
		}
		return getIsOverdue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Project Name: " + name + "\n" +
				"Project Key: " + key + "\n" +
				"Project Lead: " + lead.getDisplayName() + "\n";
	}
	

}
