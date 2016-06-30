package com.sgt.pmportal.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;

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
	 * Calculates the progress on a specific project as completed issues out of total issues
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
		//SEA=actual/estimated
		double completeDate=sprint.getCompleteDate().getTime();
		double endDate=sprint.getEndDate().getTime();
		double startDate=sprint.getStartDate().getTime();
		double estimatedDuration=endDate-startDate;
		double actualDuration=completeDate-startDate;
		double sea=(actualDuration/estimatedDuration);
		return sea;
	}
	/**
	 * calculates overall SEA (for all projects) by averaging with STD deviation
	 * 
	 * @param issue
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public ArrayList<Double> calculateProjectSEA(JiraProject project, List<Sprint> sprintList) throws IOException, ParseException{
		System.out.println("Getting sprints...");
		project.getSprints();
		if (sprintList==null){
			SprintServices sprintService=new SprintServices(client, authorization, baseURL);
			sprintList=sprintService.getClosedSprintsByProject(project);
		}
		double seaSum=0;
		double length=sprintList.size();
		System.out.println("Number of sprints: "+length);
		//this list stores individual values to calculate standard deviation
		ArrayList<Double> seaList=new ArrayList<Double>();
		System.out.print("Calculating SEA values...\n");
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
	 * calculates EEA for a single sprint, based on actual effort/estimated effort.
	 * A sprint's effort is determined for a modern JIRA instance by adding up the estimations of
	 * All its issues
	 * @param sprint
	 * @throws IOException 
	 */
	public double calculateSprintEEA(Sprint sprint) throws IOException{
		// EEA=actualEffort/estimatedEffort
		double actualEffort=0;
		double estimatedEffort=0;
		List<Issue> issueList=SprintServices.getIssuesBySprint(sprint, client);
		SprintServices sprintService=new SprintServices(client, authorization, baseURL);
		//for a modern JIRA, get estimation as a property of issues
		try{
			for (Issue issue:issueList){
				String getURL="/rest/agile/latest/issue/"+issue.getKey()+"/estimation?boardId="+sprint.getBoardId();
				String responseString=sprintService.getAgileData(getURL);
				JSONObject responseObject=new JSONObject(responseString);
				System.out.println(responseObject);
				double estimation=0;
				try{
					estimation=(Double.valueOf(responseObject.get("value").toString())).doubleValue();
				} catch(JSONException noValue){
					System.err.println("Issue does not contain an estimation!");
				}
				//if issue was added before the start date, that was in the estimation
				if (sprint.getStartDate().getTime()>(issue.getCreationDate().toDate().getTime())){
					estimatedEffort=estimatedEffort+estimation;
				}
				//the total issues present at the end represents the actual effort
				actualEffort=actualEffort+estimation;
			}
			//for older JIRAs, can find estimation in sprint report
		}catch (FileNotFoundException greenHopper){
			System.err.println("Warning: Version of Jira is outdated! Attempting to fix with Greenhopper API");
			String getURL="/rest/greenhopper/latest/rapid/charts/sprintreport?rapidViewId="+sprint.getBoardId()+"&sprintId="+sprint.getId();
			String responseString=sprintService.getAgileData(getURL);
			JSONObject responseObject=new JSONObject(responseString);
			JSONObject contentObject=responseObject.getJSONObject("contents");
			JSONObject allIssueObject=contentObject.getJSONObject("allIssuesEstimateSum");
			JSONObject puntedIssueObject=contentObject.getJSONObject("puntedIssuesEstimateSum");
			actualEffort=(Double.valueOf(allIssueObject.get("value").toString()).doubleValue());
			estimatedEffort=actualEffort- (Double.valueOf(puntedIssueObject.get("value").toString()).doubleValue());
		}
		double eea=actualEffort/estimatedEffort;
		if (actualEffort==0){
			eea=1;
		}
		return eea;
	}
	/**
	 * calculates Project EEA and standard deviation
	 * 
	 * @param project
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public ArrayList<Double> calculateProjectEEA(JiraProject project, List<Sprint> sprintList) throws JSONException, IOException, ParseException{
		SprintServices sprintService=new SprintServices(client, authorization, baseURL);
		if (sprintList==null){
			sprintList=sprintService.getClosedSprintsByProject(project);
		}
		double eeaSum=0;
		double length=sprintList.size();
		System.out.println("Number of sprints: "+length);
		//this list stores individual values to calculate standard deviation
		ArrayList<Double> eeaList=new ArrayList<Double>();
		System.out.print("Calculating EEA values...\n");
		//get eea values for every sprint
		for (Sprint sprint:sprintList){
			double eea=calculateSprintEEA(sprint);
			eeaSum=eeaSum+eea;
			eeaList.add(eea);
		}
		//calculate the average
		double averageEEA=(eeaSum/length);
		//calculate standard deviation
		double summand=0;
		for (double eea:eeaList){
			summand=summand + (eea-averageEEA) * (eea-averageEEA);
		}
		double eeaDev=Math.sqrt(summand/length);
		//store values in an array list and return
		ArrayList<Double> eeaMetric=new ArrayList<Double>();
		eeaMetric.add(averageEEA);
		eeaMetric.add(eeaDev);
		return eeaMetric;
	}
	/**
	 * calculates bugs in a project
	 * 
	 * @param project
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public long calculateBugs(String projectKey){
		//find issues listed as bugs
		long bugNum=0;
		//Long JQL query, but better performance if we let the jira server handle the sorting than converting all these
		//to issues and then filtering their statuses. Leave spaces before OR!
		Iterable<BasicIssue> issueList=client.getSearchClient().searchJql("project="+projectKey + "&status=open "
				+ "OR project="	+projectKey+"&status=\"In Progress\" "
				+ "OR project=" + projectKey + "&status=\"To Do\" "
				+ "OR project="+projectKey+"&status=\"Reopened\"",1000,0).claim().getIssues();
		//iterate through search results to find those of type bug
		for (BasicIssue issue:issueList){
			//The class BasicIssue does not contain information about the issue type, convert to JiraIssue
			String issueType=GeneralServices.toJiraIssue(issue, client).getType();
			if (Objects.equals(issueType, "Bug")){
				bugNum++;
			}
		}
		return bugNum;
	}
	/**
	 * calculates defects in a project by category
	 * 
	 * @param project
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public List<Number> calculateDefectTotal (JiraProject project) throws IOException, ParseException{
		SprintServices sprintService=new SprintServices(client, authorization, baseURL);
		List<Number> defectArray=new ArrayList<Number>();
		long bugNum=calculateBugs(project.getKey());
		long overDue=0;
		//get sprints here and pass them in to reduce repetitive load times
		List<Sprint> sprintList=sprintService.getClosedSprintsByProject(project);
		double sea=calculateProjectSEA(project, sprintList).get(0);
		double eea=calculateProjectEEA(project, sprintList).get(0);
		if (project.seeIfOverdue()){
			overDue++;
		}
		//store all values in an array so we can return a single object, indices [0]=bugs, [1]=SEA, [2]=EEA, [3]=overdue
		defectArray.add(new Long(bugNum));
		defectArray.add(new Double(sea));
		defectArray.add(new Double(eea));
		defectArray.add(new Long(overDue));
		return defectArray;
	}
	/**
	 * calculates accuracy trends of a specific project
	 * 
	 * @param project
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public ArrayList<List<Double>> predictAccuracy(JiraProject project) throws JSONException, IOException, ParseException{
		SprintServices sprintService=new SprintServices(client, authorization, baseURL);
		List<Sprint> sprintList=sprintService.getClosedSprintsByProject(project);
		//Datalist will hold seaList and eeaList to package them together
		ArrayList<List<Double>> dataList=new ArrayList<List<Double>>();
		List<Double> seaList=new ArrayList<Double>();
		List<Double> eeaList=new ArrayList<Double>();
		//check if the sprintList is empty
		if (sprintList.size()>0){
			double nextSea=0;
			double nextEea=0;
			//add all the values to their respective lists (create a vector out of them)
			for (Sprint sprint:sprintList){
				seaList.add(calculateSprintSEA(sprint));
				eeaList.add(calculateSprintEEA(sprint));
			}
			if (sprintList.size()>1){
				//using Theil-Sen estimator, which finds the median of the slopes (change in x is (i+1)-i=1)
				for (int i=0; i+1 < seaList.size();i++){
					List<Double> seaSlopeList=new ArrayList<Double>();
					seaSlopeList.add(seaList.get(i+1)-seaList.get(i));
					seaSlopeList.sort(null);
					//find the median by getting the index at the halfway point, and add that change to the last SEA value
					//Note, the size of a list is always 1 bigger than the highest index
					nextSea=seaList.get(seaList.size()-1)+seaSlopeList.get(Math.round((seaSlopeList.size()-1)/2));
				}
				for (int i=0; i+1 < eeaList.size();i++){
					List<Double> eeaSlopeList=new ArrayList<Double>();
					eeaSlopeList.add(eeaList.get(i+1)-eeaList.get(i));
					eeaSlopeList.sort(null);
					//find the median by getting the index at the halfway point, and add that change to the last EEA value
					nextEea=eeaList.get(eeaList.size()-1)+eeaSlopeList.get(Math.round((eeaSlopeList.size()-1)/2));
				}
			}else {
				//this is if there is only one sprint, in which case the prediction is that there will be no change
				nextSea=seaList.get(0);
				nextEea=eeaList.get(0);
			}
			seaList.add(nextSea);
			dataList.add(seaList);
			eeaList.add(nextEea);
			dataList.add(eeaList);
		}
		//indices [0]=seaList, [1]=eeaList
		return dataList;
	}

	/**
	 * calculates bug trends of a specific project
	 * 
	 * @param project
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public List<Long> predictBugs(JiraProject project) throws JSONException, IOException, ParseException{
		List<Long> bugList=new ArrayList<Long>();
		SprintServices sprintService=new SprintServices(client, authorization, baseURL);
		List<Sprint> sprintList=sprintService.getClosedSprintsByProject(project);
		for (Sprint sprint:sprintList){
			//get list of issues in a sprint (time consuming)
			List<Issue> issueList=SprintServices.getIssuesBySprint(sprint, client);
			long bugNum=0;
			//go through issues to find bugs
			for (Issue issue:issueList){
				if (Objects.equals(issue.getIssueType().getName(), "Bug")){
					bugNum++;
				}
			}
			//add the number of bugs in the sprint to a list
			bugList.add(bugNum);
		}
		//see if buglist is empty
		if (bugList.size()>0){
			//default value is the value of the last bug
			long nextBug=bugList.get(bugList.size()-1);
			//if there is sufficient data, use Theil-Sen to predict next value
			if (bugList.size()>1){
				//using Theil-Sen Estimator
				for (int i=0; i+1 < bugList.size();i++){
					List<Long> bugSlopeList=new ArrayList<Long>();
					bugSlopeList.add(bugList.get(i+1)-bugList.get(i));
					bugSlopeList.sort(null);
					//find the median by getting the index at the halfway point, and add that change to the last SEA value
					//Note, the size of a list is always 1 bigger than the highest index
					nextBug=bugList.get(bugList.size()-1)+bugSlopeList.get(Math.round((bugSlopeList.size()-1)/2));
				}
			}
			bugList.add(nextBug);
		}
		return bugList;
	}
}