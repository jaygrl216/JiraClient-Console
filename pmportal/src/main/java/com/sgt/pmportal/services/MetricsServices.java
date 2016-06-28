package com.sgt.pmportal.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.Sprint;

/**
 * The Metrics class is used to calculate information about the project
 * such as progress, SEA and EEA
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
	 * MetricsServices constructor gets initialized with 
	 * a JiraRestclient
	 * 
	 * @param client, 
	 * @param authorization
	 * @param baseURL
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
	public double calculateProgress(String projectKey){
		double progress;
		double completedIssues = 0;
		double total = 0;

		Iterable<BasicIssue> issueIterable=client.getSearchClient().searchJql("project="
				+ projectKey,1000,0).claim().getIssues();

		for (BasicIssue ii: issueIterable){
			total++;
			String issueStatus=client.getIssueClient().getIssue(ii.getKey()).claim().getStatus().getName();
			if (Objects.equals(issueStatus, "Closed") || Objects.equals(issueStatus, "Done")|| Objects.equals(issueStatus, "Resolved")){
				completedIssues++;
			}
		}
		progress = (completedIssues / total * 100);
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
	public static double calculateSprintSEA(Sprint sprint) throws IOException, ParseException{
		System.out.print("Calculating SEA...");
		//SEA=actual/estimated
		double completeDate=sprint.getCompleteDate().getTime();
		double endDate=sprint.getEndDate().getTime();
		double startDate=sprint.getStartDate().getTime();
		double estimatedDuration=endDate-startDate;
		double actualDuration=completeDate-startDate;
		double sea=(actualDuration/estimatedDuration);
		System.out.print("done: " + sea+"\n");
		return sea;
	}
	/**
	 * calculates overall SEA (for all projects) by averaging with STD deviation
	 * 
	 * @param issue
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public ArrayList<Double> calculateProjectSEA(JiraProject project) throws IOException, ParseException{
		System.out.println("Getting sprints...");
		SprintServices sprintService=new SprintServices(client, authorization, baseURL);
		ArrayList<Sprint> sprintList=sprintService.getClosedSprintsByProject(project);

		double seaSum=0;
		double length=sprintList.size();
		System.out.println("Number of sprints: "+length);
		ArrayList<Double> seaList=new ArrayList<Double>();
		System.out.print("Retrieving SEA values...\n");
		//get sea values for every sprint
		for (Sprint sprint:sprintList){
			double sea=calculateSprintSEA(sprint);
			seaSum=seaSum+sea;
			seaList.add(sea);
		}
		//calculate the average
		double averageSEA=(seaSum/length);
		//calculate standard deviation
		double summand=0;
		for (double sea:seaList){
			summand=summand + (sea-averageSEA) * (sea-averageSEA);
		}
		double seaDev=Math.sqrt(summand/length);

		//store values in an array list and return
		ArrayList<Double> seaMetric=new ArrayList<Double>();
		seaMetric.add(averageSEA);
		seaMetric.add(seaDev);
		return seaMetric;
	}
	/**
	 * calculates EEA
	 * 
	 * @param sprint
	 */
	public void calculateSprintEEA(Sprint sprint){
		// EEA=actualEffort/estimatedEffort
		double actualEffort=0;
		double estimatedEffort=0;
		SprintServices sprintService=new SprintServices(client, authorization, baseURL);
		ArrayList<Issue> issueList=sprintService.getIssuesBySprint(sprint);
		for (Issue issue:issueList){
			if (sprint.getStartDate().before(issue.getCreationDate().toDate())){
				actualEffort=actualEffort + (double) issue.getFieldByName("estimation").getValue();
			}
		}
		double eea=actualEffort/estimatedEffort;
		//TODO
	}
	/**
	 * calculates EEA
	 * 
	 * @param project
	 */
	public void calculateProjectEEA(JiraProject project){
		// EEA=actualEffort/estimatedEffort
		double actualEffort=0;
		double estimatedEffort=0;
		double eea=actualEffort/estimatedEffort;
		//TODO
	}

	/**
	 * calculates defects in all projects
	 * 
	 * @param project
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public List<Long> calculateDefectTotal () throws IOException, ParseException{
		ProjectServices projectService=new ProjectServices(client, authorization, baseURL);
		List<JiraProject> projectList=projectService.getAllJiraProjects();
		ArrayList<Long> defectArray=new ArrayList<Long>();
		long seaDefect=0;
		long eeaDefect=0;
		long bugNum=0;
		long overDue=0;
		for (JiraProject project:projectList){
			Iterable<BasicIssue> issueList=client.getSearchClient().searchJql("project="+project.getKey(),1000,0).claim().getIssues();
			for (BasicIssue issue:issueList){
				String issueType=GeneralServices.toJiraIssue(issue, client).getType();
				if (Objects.equals(issueType, "Bug")){
					bugNum++;
				}
			}
			if (calculateProjectSEA(project).get(0)< 0.9 || calculateProjectSEA(project).get(0) > 1.1){
				seaDefect++;
			}
			if (project.seeIfOverdue()){
				overDue++;
			}
		}
		defectArray.add(bugNum);
		defectArray.add(seaDefect);
		defectArray.add(eeaDefect);
		defectArray.add(overDue);
		return defectArray;
	}
	/**
	 * calculates trends of a specific project
	 * 
	 * @param project
	 */
	public void predictTrend(JiraProject project){

	}
}
