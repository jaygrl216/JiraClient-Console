package com.sgt.pmportal.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
	protected JiraUser lead;
	protected String description;
	protected URI uri;
	protected List<Release> releases;
	protected List<Sprint> sprints;
	protected List<JiraIssue> issues;
	protected Date due;
	protected double velocity;
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
	public JiraProject(String name, String key, JiraUser lead, String description, 
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
		sprints = new ArrayList<>();
		due = setDefaultDueDate();
		velocity = 0;
	}

	/**
	 * Constructor for JiraProject with a given issueList
	 * 
	 * @param name
	 * @param key
	 * @param lead
	 * @param description
	 * @param releases
	 * @param uri
	 * @param issueList
	 */
	public JiraProject(String name, String key, JiraUser lead, String description,
			List<Release> releases, URI uri, List<JiraIssue> issueList) {
		this.name = name;
		this.key = key;
		this.lead = lead;
		this.description = description;
		this.releases = new ArrayList<>(releases);
		this.uri = uri;
		isComplete = false;
		isOverdue = false;
		issues = new ArrayList<>(issueList);
		sprints = new ArrayList<>();
		due = setDefaultDueDate();
		velocity = 0;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public String getLead() {
		return lead.getFullName();
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
	
	/**
	 * Gets the number of versions this project has
	 * 
	 * @return int
	 */
	public int getNumRelease() {
		return releases.size();
	}
	
	/**
	 * Returns the due date of the project
	 * @return
	 */
	public Date getDueDate() {
		return due;
	}
	
	/**
	 * Sets the default project due date based off the latest release due date
	 * 
	 * @return Date
	 */
	private Date setDefaultDueDate() {
		Date latest = new Date(0);

		for (Release curRelease: releases) {
			if(curRelease.getRelease() != null) {
				Date curDate = curRelease.getRelease().toDate();
				if (curDate.compareTo(latest) > 0) {
					latest = curDate;
				}
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
	
	public void addSprints(ArrayList<Sprint> sprints) {
		this.sprints = new ArrayList<Sprint>(sprints);
	}
	
	
	public List<Sprint> getSprints() {
		return sprints;
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
	
	/**
	 * Gets whether a project is overdue or not
	 * 
	 * @return boolean
	 */
	public boolean getIsOverdue() {
		return isOverdue;
	}

	/**
	 * Changes and returns isOverdue based on if date is past the due date
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
	
	/* The following two methods are the velocity of the project. 
	 * Velocity is calculated by totalSEA for closed sprints divided by number
	 * of closed sprints
	 */
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	
	public double getVelocity() {
		return velocity;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Project Name: " + name + "\n" +
				"Project Key: " + key + "\n" +
				"Project Lead: " + lead.getFullName() + "\n";
	}

	public int sprintsWorked() {
		int sprintsWorked = 0;
		for (Sprint s: getSprints()) {
			if(s.isClosed() || s.isOpen()){
				sprintsWorked++;
			}
		}
		
		return sprintsWorked;
	}
	
	public int sprintsNotWorked() {
		int sprintsNotWorked = 0;
		for (Sprint s: getSprints()) {
			if(s.isFuture()){
				sprintsNotWorked++;
			}
		}
		
		return sprintsNotWorked;
	}


}
