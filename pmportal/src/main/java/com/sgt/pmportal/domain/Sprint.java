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
	private static int CLOSED = -1;
	private static int OPEN = 0;
	private static int FUTURE = 1;
	protected String name;
	protected String id;
	protected String state;
	protected Date startDate;
	protected Date endDate;
	protected Date completeDate;
	protected String boardId;
	protected int status;

	/**
	 * Constructor for Sprint object
	 * 
	 * @param name
	 * @param id
	 * @param state
	 * @param startDate
	 * @param endDate
	 * @param completeDate
	 * @param boardId
	 * @param status
	 */
	public Sprint(String name, String id, String state, Date startDate, 
			Date endDate, Date completeDate, String boardId, int status) {
		this.name = name;
		this.id = id;
		this.state = state;
		this.startDate = startDate;
		this.endDate = endDate;
		this.completeDate = completeDate;
		this.boardId=boardId;
		this.status = status;
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
		return status == CLOSED;
	}
	
	public boolean isOpen() {
		return status == OPEN;
	}
	
	public boolean isFuture() {
		return status == FUTURE;
	}
	
	
	
}
