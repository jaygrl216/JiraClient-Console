package com.sgt.pmportal.domain;

import org.joda.time.DateTime;
import org.json.JSONObject;

/**
 * This class represents an issue in Jira.
 * 
 * @author Jada Washington
 * @author Aman Mital
 *
 */
public class JiraIssue {
	protected String type;
	protected String priority;
	protected String key;
	protected String desc;
	protected String user;
	protected DateTime create;
	protected DateTime due;
	protected String status;

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
	 * @param status
	 */
	public JiraIssue(String key, String type, String p, String desc, 
			String user, DateTime create, DateTime due, String status) {
		this.key = key;
		this.desc = desc;
		this.user = user;
		this.create = create;
		this.due = due;
		this.status = status;
		this.type = type;
		this.priority = p;

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
		return type;
	}

	public String getPriority() {
		return priority;
	}

	public DateTime getCreationDate() {
		return create;
	}

	public DateTime getDueDate() {
		return due;
	}
	public String getStatus() {
		return status;
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

	/**
	 * Returns a JSON formatted String
	 * @return String
	 */
	public String JSONString() {
		JSONObject issue = new JSONObject();
		issue.put("key", key);
		issue.put("type", getType());
		issue.put("description", desc);
		issue.put("priority", getPriority());
		issue.put("assignee", user);
		issue.put("created", create);
		issue.put("due", due);
		issue.put("status", status);
		return issue.toString();	
	}

}
