package com.sgt.pmportal.domain;

import org.joda.time.DateTime;

/**
 * This class represents a issue on Jira.
 * 
 * @author Jada Washington
 * @author Aman Mital
 *
 */
public class JiraIssue {
	private enum IssueType {STORY, TASK, EPIC, BUG }
	private enum Priority {LOW, MEDIUM, HIGH}
	protected String key;
	protected IssueType type;
	protected Priority p;
	protected String desc;
	protected String user;
	protected DateTime create;
	protected DateTime due;
	
	/**
	 * Constructor for JiraIssue
	 * 
	 * @param key
	 * @param type
	 * @param p
	 * @param desc
	 * @param user
	 * @param create
	 * @param due
	 */
	public JiraIssue(String key, String type, String p, String desc, 
			String user, DateTime create, DateTime due) {
		this.key = key;
		this.desc = desc;
		this.user = user;
		this.create = create;
		this.due = due;

		switch (type) {
		case "Story":
			this.type = IssueType.STORY;
			break;
		case "Epic":
			this.type = IssueType.EPIC;
			break;
		case "Task":
			this.type = IssueType.TASK;
			break;
		case "Bug":
			this.type=IssueType.BUG;
			break;
		default:
			this.type = null;
			break;
		}
try{
		switch (p) {
		case "Low":
			this.p = Priority.LOW;
			break;
		case "Medium":
			this.p = Priority.MEDIUM;
			break;
		case "High":
			this.p = Priority.HIGH;
			break;
		}
}catch (NullPointerException noPriority){
	this.p=null;
}
	}

	public String getKey() {
		return key;
	}

	public String getDescription() {
		return desc;
	}

	public String getUser() {
		return user;
	}

	public String getType() {
		try{
		switch (type) {
		case STORY:
			return "Story";
		case EPIC:
			return "Epic";
		case TASK:
			return "Task";
		case BUG:
			return "Bug";
		default:
			return null;
		}
		}catch(NullPointerException noType){
		}return null;
	}

	public String getPriority() {
		switch (p) {
		case LOW:
			return "Low";
		case MEDIUM:
			return "Medium";
		case HIGH:
			return "High";
		default:
			return null;
		}
	}

	public DateTime getCreationDate() {
		return create;
	}

	public DateTime getDueDate() {
		return due;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("Key:" + key);
		if (type != null) {
			str.append(" Type: " + getType());
		} else {
			str.append(" Type: None");
		}
		return str.toString();
	}

}
