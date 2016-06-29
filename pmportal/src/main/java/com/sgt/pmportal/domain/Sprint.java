package com.sgt.pmportal.domain;

import java.util.Date;

/**
 * This class represents a Sprint in a Jira project
 * 
 * @author Aman Mital
 * @author Jada Washington
 *
 */
public class Sprint {
	protected String name;
	protected String id;
	protected String state;
	protected Date startDate;
	protected Date endDate;
	protected Date completeDate;
	protected String boardId;

	/**
	 * Constructor for Sprint object
	 * 
	 * @param name
	 * @param id
	 * @param state
	 * @param startDate
	 * @param endDate
	 * @param completeDate
	 */
	public Sprint(String name, String id, String state, Date startDate, Date endDate, Date completeDate, String boardId) {
		this.name = name;
		this.id = id;
		this.state = state;
		this.startDate = startDate;
		this.endDate = endDate;
		this.completeDate = completeDate;
		this.boardId=boardId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getState() {
		return state;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Date getCompleteDate() {
		return completeDate;
	}
	public String getBoardId() {
		return boardId;
	}
	public boolean isClosed() {
		return true;
	}
	
	public boolean isOpen() {
		return true;
	}
	
	
	
}
