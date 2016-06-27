package com.sgt.pmportal.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.Sprint;

/**
 * The Metrics class is used to calculate information about the project
 * such as progress, SEA and work effort
 * 
 * @author Aman Mital
 * @author Jada Washington
 *
 */
public class MetricsServices {
	JiraRestClient client;
	String authorization;
	String baseURL;
	/**
	 * MetricsServices constructor gets initilaized with 
	 * a JiraRestclient
	 * 
	 * @param client, authorization, base URL
	 */
	public MetricsServices(JiraRestClient client, String authorization, String baseURL){
		this.client = client;
		this.authorization=authorization;
		this.baseURL=baseURL;
	}

	/**
	 * Calculates the progress on a specific project
	 * 
	 * @param projectKey
	 * @return double
	 */
	public String calculateProgress(String projectKey){
		long progressCalc;
		String progress="0%";
		double completedIssues = 0;
		double total = 0;
		Iterable<BasicIssue> issueIterable=client.getSearchClient().searchJql("project="
				+ projectKey,1000,0).claim().getIssues();
		for (BasicIssue ii: issueIterable){
			total++;
			if (Objects.equals(client.getIssueClient().getIssue(
					ii.getKey()).claim().getStatus().getName(), "Done")){
				completedIssues++;
			}
		}
		progressCalc = Math.round(completedIssues / total * 100);
		progress=progressCalc+"%";
		return progress;
	}

	/**
	 * calculates SEA of a specific project using Sprints
	 * 
	 * @param project
	 * @return 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Long calculateProjectSEA(JiraProject project) throws IOException, ParseException{
	SprintServices sprintService=new SprintServices(client, authorization, baseURL);
	ArrayList<Sprint> sprintList=sprintService.getClosedSprintsByProject(project);
	Date completeDate = null;
	Date endDate=null;
	//gets the total number of sprints
	double numSprints=sprintList.size();
	//the number of 'good' sprints starts at the max value
	double goodSprint=sprintList.size();
	for (Sprint sprint:sprintList){
		completeDate=sprint.getCompleteDate();
		endDate=sprint.getEndDate();
		//if the scheduled end date is before the complete date, the sprint is 'bad'
		if (endDate.before(completeDate)){
			goodSprint--;
		}
System.out.println("Sprint: " + sprint.getName());
		System.out.println("Completed: "+completeDate);
		System.out.println("Scheduled to complete: "+endDate);
	}
	long sea=Math.round(goodSprint/numSprints * 100);
	return sea;
	}

	/**
	 * calculates overall SEA (for all projects) by averaging with STD deviation
	 * 
	 * @param issue
	 * @throws ParseException 
	 * @throws IOException 
	 */
public Long calculateOverallSEA() throws IOException, ParseException{
	ProjectServices pService=new ProjectServices(client);
	List<JiraProject> projectList=pService.getAllJiraProjects();
	double seaSum=0;
	double length=projectList.size();
	ArrayList<Long> seaList=new ArrayList<Long>();
	//get sea values for every project
	for (JiraProject project:projectList){
		Long sea=calculateProjectSEA(project);
		seaSum=seaSum+sea;
		seaList.add(sea);
	}
	Long averageSEA=Math.round(seaSum/length);
	//calculate standard deviation
	for (Long sea:seaList){
	
	}
	return averageSEA;
}
	
	/**
	 * calculates EEA
	 * 
	 * @param project
	 */
	public void calculateProjectEEA(JiraProject project){
		// will complete
		throw new UnsupportedOperationException();

	}

	/**
	 * calculates defects in all projects
	 * 
	 * @param project
	 */
	public void calculateDefectTotal (ArrayList<JiraProject> projectList){

	}
	/**
	 * calculates trends of a specific project
	 * 
	 * @param project
	 */
	public void predictTrend(JiraProject project){

	}
}
