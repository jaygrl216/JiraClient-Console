package com.sgt.pmportal.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
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
	public double calculateSprintSEA(Sprint sprint) throws IOException, ParseException{
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
