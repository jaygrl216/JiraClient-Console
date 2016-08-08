package com.sgt.pmportal.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.sgt.pmportal.domain.JiraIssue;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.Sprint;

/**
 * The Metrics class is used to calculate information about the project such as
 * progress, SEA, EEA, bug number, and predictions for these
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
	 * MetricsServices constructor gets initialized with a JiraRestclient
	 * 
	 * @param client,
	 * @param authorization
	 * @param baseURL
	 */
	public MetricsServices(JiraRestClient client, String authorization, String baseURL) {
		this.client = client;
		this.authorization = authorization;
		this.baseURL = baseURL;
	}

	/**
	 * Calculates the progress on a specific project as completed issues out of
	 * total issues
	 * 
	 * @param projectKey
	 * @return double
	 */
	public double calculateProgress(String projectKey) {
		double progress;
		double completedIssues = 0;
		double total = 0;
		Iterable<BasicIssue> issueComplete = client
				.getSearchClient().searchJql("project=" + projectKey + "&status=closed " 
		+ "OR project=" + projectKey
						+ "&status=done " + "OR project=" + projectKey + "&status=resolved ", 1000, 0)
				.claim().getIssues();
		for (@SuppressWarnings("unused")
		BasicIssue i : issueComplete) {
			completedIssues++;

		}
		Iterable<BasicIssue> issueAll = client.getSearchClient().searchJql("project=" 
		+ projectKey, 1000, 0).claim().getIssues();
		for (@SuppressWarnings("unused")
		BasicIssue issue : issueAll) {
			total++;
		}
		progress = (completedIssues / total * 100);
		return progress;
	}

	/**
	 * Calculates the SEA of a sprint
	 * 
	 * @param project
	 * @return double
	 * @throws IOException
	 * @throws ParseException
	 */
	public static double calculateSprintSEA(Sprint sprint) throws IOException, ParseException {
		// SEA=actual/estimated
		double completeDate = sprint.getCompleteDate().getTime();
		double endDate = sprint.getEndDate().getTime();
		double startDate = sprint.getStartDate().getTime();
		double estimatedDuration = endDate - startDate;
		double actualDuration = completeDate - startDate;
		double sea = (actualDuration / estimatedDuration);
		return sea;
	}

	/**
	 * Calculates overall SEA (for a project) by averaging the SEA of sprints with STD deviation
	 * 
	 * @param issue
	 * @throws ParseException
	 * @throws IOException
	 * @return List<Double>
	 */
	public Double calculateProjectSEA(JiraProject project, List<Sprint> sprintList)
			throws IOException, ParseException {
		if (sprintList == null) {
			SprintServices sprintService = new SprintServices(client, authorization, baseURL);
			sprintList = sprintService.getClosedSprintsByProject(project);
		}
		double seaSum = 0;
		double length = sprintList.size();
		if (length==0){
			return 1.0;
		};
		System.out.println("Number of sprints: " + length);
		// get sea values for every sprint
		for (Sprint sprint : sprintList) {
			double sea = calculateSprintSEA(sprint);
			seaSum = seaSum + sea;
		}
		// calculate the average
		double averageSEA = (seaSum / length);
		return averageSEA;
	}

	/**
	 * Calculates the EEA for a single sprint, based on actual effort/estimated
	 * effort. A sprint's effort is determined for a modern JIRA instance by
	 * adding up the estimations of all its issues
	 * 
	 * @param sprint
	 * @throws IOException
	 * @return double
	 */
	public double calculateSprintEEA(Sprint sprint) throws IOException {
		// EEA=actualEffort/estimatedEffort
		double actualEffort = 0;
		double estimatedEffort = 0;
		SprintServices sprintService = new SprintServices(client, authorization, baseURL);
		List<JiraIssue> issueList = sprintService.getIssuesBySprint(sprint, client);

		// for a modern JIRA, get estimation as a property of issues
		try {
			for (JiraIssue issue : issueList) {
				String getURL = "/rest/agile/latest/issue/" + issue.getKey() + "/estimation?boardId="
						+ sprint.getBoardId();
				String responseString = sprintService.getAgileData(getURL);
				JSONObject responseObject = new JSONObject(responseString);
				double estimation = 0;
				try {
					estimation = (Double.valueOf(responseObject.get("value").toString())).doubleValue();
				} catch (JSONException noValue) {
					System.err.println("Issue does not contain an estimation!");
				}
				// if issue was added before the start date, that was in the
				// estimation
				if (sprint.getStartDate().getTime() > (issue.getCreationDate().toDate().getTime())) {
					estimatedEffort = estimatedEffort + estimation;
				}
				// the total issues present at the end represents the actual
				// effort
				actualEffort = actualEffort + estimation;
			}
			// for older JIRAs, can find estimation in sprint report
		} catch (FileNotFoundException greenHopper) {
			System.err.println("Warning: Version of Jira is outdated! Attempting to fix with Greenhopper API");
			String getURL = "/rest/greenhopper/latest/rapid/charts/sprintreport?rapidViewId=" + sprint.getBoardId()
					+ "&sprintId=" + sprint.getId();
			String responseString = sprintService.getAgileData(getURL);
			JSONObject responseObject = new JSONObject(responseString);
			JSONObject contentObject = responseObject.getJSONObject("contents");
			JSONObject allIssueObject = contentObject.getJSONObject("allIssuesEstimateSum");
			JSONObject puntedIssueObject = contentObject.getJSONObject("puntedIssuesEstimateSum");
			actualEffort = (Double.valueOf(allIssueObject.get("value").toString()).doubleValue());
			estimatedEffort = actualEffort - (Double.valueOf(puntedIssueObject.get("value").toString()).doubleValue());
		}
		double eea = actualEffort / estimatedEffort;
		if (actualEffort == 0) {
			eea = 1;
		}
		return eea;
	}

	/**
	 * Calculates Project EEA and standard deviation
	 * 
	 * @param project
	 * @throws ParseException
	 * @throws IOException
	 * @throws JSONException
	 */
	public Double calculateProjectEEA(JiraProject project, List<Sprint> sprintList)
			throws IOException, ParseException {
		SprintServices sprintService = new SprintServices(client, authorization, baseURL);
		if (sprintList == null) {
			sprintList = sprintService.getClosedSprintsByProject(project);
		}
		double eeaSum = 0;
		double length = sprintList.size();
		if (length==0){
			return 1.0;
		};
		// get eea values for every sprint
		for (Sprint sprint : sprintList) {
			double eea = calculateSprintEEA(sprint);
			eeaSum = eeaSum + eea;
		}
		// calculate the average
		double averageEEA = (eeaSum / length);
		return averageEEA;
	}

	/**
	 * Calculates the number of open bugs in a project
	 * 
	 * @param project
	 * @throws ParseException
	 * @throws IOException
	 */
	public long calculateBugs(String projectKey) {
		// find issues listed as bugs
		long bugNum = 0;
		// Long JQL query, but better performance if we let the jira server
		// handle the sorting than converting all these
		// to issues and then filtering their statuses. Leave spaces before OR!
		Iterable<BasicIssue> issueList = client.getSearchClient()
				.searchJql("project=" + projectKey + "&status=open&type=Bug " + "OR project=" + projectKey
						+ "&status=\"In Progress\"&type=bug " + "OR project=" + projectKey
						+ "&status=\"To Do\"&type=bug " + "OR project=" + projectKey + "&status=\"Reopened\"&type=bug",
						1000, 0)
				.claim().getIssues();
		// iterate through search results to find those of type bug
		for (@SuppressWarnings("unused")
		BasicIssue issue : issueList) {
			bugNum++;
		}
		return bugNum;
	}

	/**
	 * Calculates defects in a project by category
	 * 
	 * @param project
	 * @throws ParseException
	 * @throws IOException
	 * @return List<Number>
	 */
	public List<Number> calculateDefectTotal(JiraProject project) throws IOException, ParseException {
		SprintServices sprintService = new SprintServices(client, authorization, baseURL);
		List<Number> defectArray = new ArrayList<Number>();
		long bugNum = calculateBugs(project.getKey());
		long overDue = 0;
		// get sprints here and pass them in to reduce repetitive load times
		List<Sprint> sprintList = sprintService.getClosedSprintsByProject(project);
		double sea = calculateProjectSEA(project, sprintList);
		double eea = calculateProjectEEA(project, sprintList);
		if (project.seeIfOverdue()) {
			overDue++;
		}
		// store all values in an array so we can return a single object,
		// indices [0]=bugs, [1]=SEA, [2]=EEA, [3]=overdue
		defectArray.add(new Long(bugNum));
		defectArray.add(new Double(sea));
		defectArray.add(new Double(eea));
		defectArray.add(new Long(overDue));
		return defectArray;
	}

	/**
	 * Calculates next metric values in a specific project
	 * 
	 * @param project
	 * @throws ParseException
	 * @throws IOException
	 * @throws JSONException
	 * @return List<List<Double>>
	 */
	public List<List<Double>> predictNext(JiraProject project) throws IOException, ParseException {
		SprintServices sprintService = new SprintServices(client, authorization, baseURL);
		List<Sprint> sprintList = sprintService.getClosedSprintsByProject(project);

		// Datalist will hold seaList and eeaList to package them together
		ArrayList<List<Double>> dataList = new ArrayList<List<Double>>();
		List<Double> seaList = new ArrayList<Double>();
		List<Double> eeaList = new ArrayList<Double>();
		List<Double> bugList = new ArrayList<Double>();
		// check if the sprintList is empty
		if (sprintList.size() > 0) {
			double nextSea = 0;
			double nextEea = 0;
			double nextBug = 0;
			// add all the values to their respective lists (create a vector out
			// of them)
			for (Sprint sprint : sprintList) {
				seaList.add(calculateSprintSEA(sprint));
				eeaList.add(calculateSprintEEA(sprint));
				Iterable<BasicIssue> issueList = client.getSearchClient()
						.searchJql("sprint= " + sprint.getId() + "&type=Bug ORDER BY createdDate", 1000, 0).claim()
						.getIssues();
				double bugNum = 0;
				// go through issues to find bugs
				for (@SuppressWarnings("unused")
				BasicIssue issue : issueList) {
					bugNum++;
				}
				// add the number of bugs in the sprint to a list
				bugList.add(bugNum);
			}
			if (sprintList.size() > 1) {
				// using Theil-Sen estimator, which finds the median of the
				// slopes (change in x is (i+1)-i=1)
				double seaSlope = getRegressionSlope(seaList);
				nextSea = seaList.get(seaList.size() - 1) + seaSlope;
				double eeaSlope = getRegressionSlope(eeaList);
				nextEea = eeaList.get(eeaList.size() - 1) + eeaSlope;
			} else {
				// this is if there is only one sprint, in which case the
				// prediction is that there will be no change
				nextSea = seaList.get(0);
				nextEea = eeaList.get(0);
			}
			if (bugList.size() > 0) {
				// default value is the value of the last bug
				nextBug = bugList.get(bugList.size() - 1);
				// if there is sufficient data, use Theil-Sen to predict next
				// value
				if (bugList.size() > 1) {
					double bugSlope = getRegressionSlope(bugList);
					nextBug = bugList.get(bugList.size() - 1) + Math.round(bugSlope);
				}
			}

			seaList.add(nextSea);
			dataList.add(seaList);
			eeaList.add(nextEea);
			dataList.add(eeaList);
			bugList.add(nextBug);
			dataList.add(bugList);
		}
		// indices [0]=seaList, [1]=eeaList, [2]=bugList
		return dataList;
	}
	
	/**
	 * Gets the regression slope from a list of data (assumes that the independent variable is from 0...N in increments of 1
	 * 
	 * @param data
	 * @return
	 */
	public double getRegressionSlope(List<Double> data) {
		List<Double> slopeList = new ArrayList<>();
		double slope = 0;
		// Theil-Sen estimator
		for (int i = 0; i + 1 < data.size(); i++) {
			slopeList.add(data.get(i + 1) - data.get(i));
			slopeList.sort(null);
			// find the median slope by getting the index at the halfway point,
			// and add that to the last value
		}
		slope = slopeList.get(Math.round((slopeList.size() - 1) / 2));
		return slope;
	}

	/**
	 * Calculates the regression error of a set of data
	 * points. This assumes that x starts at 0 and is in
	 * increments of 1 and y (data) is a one dimensional vector
	 * 
	 * @param data,regressionLineSlope
	 * @throws ParseException
	 * @throws IOException
	 * @throws JSONException
	 * @return Double
	 */
	public Double getRegressionError(List<Double> data, Double regressionSlope) {
		if (regressionSlope == null) {
			regressionSlope = getRegressionSlope(data);
		}
		// standard deviation on the error of the regression line
		double s = 0;
		// this calculation will only work if there are at least four observed
		// data points
		if (data.size() > 4) {
			double sumE = 0;
			// [observed value1 ] index:0
			// [observed value2 ] index:1
			// [ ... ] index:2...N-1
			// [observed valueN ] index:N size x
			// [estimated valueX] index:X
			// x is the x value (same as index) for the estimated data point
			double x = data.size() - 1;
			// number of observed data points
			double N = x;
			// 'y-intercept'
			double b = data.get(0);
			for (int i = 0; i < x; i++) {
				// error of a data point
				double e = data.get(i) - (regressionSlope * i + b); // mx+b,
																	// substitute
																	// i for x
				// sum of the squares of the error
				sumE = sumE + e * e;
			}
			// variance 'v' of the error
			double v = sumE / (N - 2); // N-2, two degrees of freedom (slope and
										// point)
			s = Math.sqrt(v);
		}else{
			s=0;
		}
		// [0]=forecast interval, [1]=error of the regression
		return s;
	}
	
	public List<Double> getAverageSEAAndEEA() throws IOException, ParseException {
		ProjectServices projectService = new ProjectServices(client, 
				authorization, baseURL);
		
		List<JiraProject> projects = projectService.getAllJiraProjects();
		List<Double> averages = new ArrayList<Double>();
		double totalSEA = 0;
		double totalEEA = 0;
		int total = 0;
		
		for(JiraProject project: projects) {
			Double curSEA = calculateProjectSEA(project, null);
			Double curEEA = calculateProjectEEA(project, null);
			if(! ((Double) curEEA).isNaN()) {
				totalSEA += curSEA;
				totalEEA += curEEA;
				total++;
			}
		}
		
		double averageSEA = totalSEA / total ;
		double averageEEA = totalEEA / total ;
		averages.add(averageSEA);
		averages.add(averageEEA);
		
		return averages;
		
	}
}
