package com.sgt.pmportal.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.util.concurrent.Promise;
import com.sgt.pmportal.domain.JiraIssue;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.JiraUser;
import com.sgt.pmportal.domain.Release;
import com.sgt.pmportal.domain.Sprint;

/**
 * The ProjectServices class gathers information from the client and creates
 * a JiraProject from the information given by that client
 * 
 * @author Jada Washington
 * @author Aman Mital
 *
 */
public class ProjectServices {
	ProjectRestClient client;
	static JiraRestClient mainClient;
	String baseURL;
	String authorization;

	/**
	 * Constructor 
	 * @param client
	 * @param authorization
	 * @param baseURL
	 */
	public ProjectServices(JiraRestClient client, String authorization, String baseURL) {
		this.client = client.getProjectClient();
		mainClient = client;
		this.baseURL = baseURL;
		this.authorization = authorization;
	}

	/**
	 * converts projects of the JRJC Project type to a JiraProject
	 * 
	 * @return ArrayList<JiraProject>
	 */
	public List<JiraProject> getAllJiraProjects() {
		ArrayList<Project> list = getAllProjects();
		ArrayList<JiraProject> jiraProjects = new ArrayList<>();
		for(Project p: list) {
			jiraProjects.add(toJiraProject(p, null));
		}
		return jiraProjects;
	}

	/**
	 * returns a list of the projects on the client
	 * 
	 * @return ArrayList<Project>
	 */
	private ArrayList<Project> getAllProjects() {
		ArrayList<Project> projectList = new ArrayList<>();
		Promise<Iterable<BasicProject>> projects = client.getAllProjects();
		Iterable<BasicProject> list = projects.claim();
		for(BasicProject bp: list) {
			Promise<Project> curProject = client.getProject(bp.getKey());
			projectList.add(curProject.claim());
		}
		return projectList;	
	}

	/**
	 * Returns a Jira project with the specified name or null if no 
	 * such project exists
	 * 
	 * @param name
	 * @return JiraProject or <code>null</null>
	 */
	public JiraProject getProjectByName(String name) {
		ArrayList<Project> projects = getAllProjects();
		for(Project p: projects) {
			if(p.getName().equalsIgnoreCase(name)) {
				return toJiraProject(p, null);
			}
		}
		throw new NullPointerException();
	}

	/**
	 * Returns a JiraProject with a specific key
	 * 
	 * @param key
	 * @return JiraProject
	 */
	public JiraProject getProjectByKey(String key) {
		return toJiraProject(mainClient.getProjectClient().getProject(key).claim(), null);
	}

	/**
	 * Returns a JiraProject implementation of a Project type
	 * 
	 * @param p
	 * @param issueList
	 * @return
	 */
	public JiraProject toJiraProject(Project p, List<JiraIssue> issueList) {
		JiraUser user = GeneralServices.toJiraUser(p.getLead(), mainClient);
		if(issueList == null) {
			return new JiraProject(p.getName(), p.getKey(), user, 
					p.getDescription(), versionsToRelease(p.getVersions()), p.getUri());
		}
		return new JiraProject(p.getName(), p.getKey(), user, 
				p.getDescription(), versionsToRelease(p.getVersions()), p.getUri(), issueList);


	}

	/**
	 * Returns a release array from an iterable object of versions
	 * @param versions
	 * @return
	 */
	private ArrayList<Release> versionsToRelease(Iterable<Version> versions) {
		ArrayList<Release> releases = new ArrayList<>();
		for(Version v: versions) {
			releases.add(new Release(v.getName(), v.getId(), v.getReleaseDate(), v.getSelf()));
		}
		return releases;
	}

	/**
	 * Converts BasicIssues to Issues and then adds them to the
	 * JiraProject it belongs too.
	 * @param p
	 * @return JiraProject
	 */
	public JiraProject toJiraProjectWithIssues (Project p) {
		Promise<SearchResult> result = mainClient.getSearchClient().searchJql(
				"project=" + p.getKey(),1000,0);
		SearchResult issues = result.claim();

		IssueRestClient issueClient = mainClient.getIssueClient();

		ArrayList<JiraIssue> issueList = new ArrayList<>();

		for (BasicIssue i : issues.getIssues()) {
			Promise<Issue> issueProm = issueClient.getIssue(i.getKey());
			Issue curIssue = issueProm.claim();

			//the following try and catch methods will prevent null pointer exceptions
			String description = curIssue.getDescription();
			String assigneeName = (curIssue.getAssignee() == null) ? null :
				curIssue.getAssignee().getDisplayName();
			String priority = (curIssue.getPriority() == null) ? null : 
				curIssue.getPriority().getName();
			DateTime creationDate = curIssue.getCreationDate();
			DateTime dueDate = curIssue.getDueDate();
			String status=curIssue.getStatus().getName();

			JiraIssue jiraIssue = new JiraIssue(curIssue.getKey(), 
					curIssue.getIssueType().getName(), priority,
					description, assigneeName, creationDate, dueDate, status);

			issueList.add(jiraIssue);
		}

		return toJiraProject(p, issueList);
	}

	/**
	 * Adds issues to a JiraProject
	 * @param jp
	 */
	public static void populateIssues(JiraProject jp) {
		Promise<SearchResult> result = mainClient.getSearchClient().searchJql(
				"project=" + jp.getKey(),1000,0);
		SearchResult issues = result.claim();

		IssueRestClient issueClient = mainClient.getIssueClient();

		for (BasicIssue i : issues.getIssues()) {
			Promise<Issue> issueProm = issueClient.getIssue(i.getKey());
			Issue curIssue = issueProm.claim();

			//the following try and catch methods will prevent null pointer exceptions
			String description = curIssue.getDescription();
			String assigneeName = (curIssue.getAssignee() == null) ? null :
				curIssue.getAssignee().getDisplayName();
			String priority = (curIssue.getPriority() == null) ? null : 
				curIssue.getPriority().getName();
			DateTime creationDate = curIssue.getCreationDate();
			DateTime dueDate = curIssue.getDueDate();
			String status=curIssue.getStatus().getName();


			JiraIssue jiraIssue = new JiraIssue(curIssue.getKey(), 
					curIssue.getIssueType().getName(), priority,
					description, assigneeName, creationDate, dueDate, status);

			jp.addToIssues(jiraIssue);
		}
	}

	/**
	 * 
	 * @param project
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public double getVelocityForProject(JiraProject project) throws IOException, ParseException {
		SprintServices sprintServs = new SprintServices(mainClient, authorization, baseURL);
		double totalSEA = 0;
		ArrayList<Sprint> sprints = sprintServs.getClosedSprintsByProject(project);
		for (Sprint s: sprints) {
			double sea = MetricsServices.calculateSprintSEA(s);
			totalSEA += sea;
		}
		return totalSEA/sprints.size();
	}

	/**
	 * If EEA and/or SEA is not good then will return and arbitary number to 
	 * reveal that planning needs to be improved.
	 * 
	 * If both are good then the number 100 will be return to signify planning 
	 * has worked well for this project 
	 * 
	 * @param project
	 * @return int
	 * @throws IOException
	 * @throws ParseException
	 */
	public int projectPlanningStatus (JiraProject project) throws IOException, ParseException {
		MetricsServices metrics = new MetricsServices(mainClient, baseURL, authorization);
		ArrayList<Double> projectSEA = metrics.calculateProjectSEA(project, 
				project.getSprints());
		ArrayList<Double> projectEEA = metrics.calculateProjectEEA(project, 
				project.getSprints());

		double total = 0;
		double total2 = 0;

		for (Double d: projectSEA) {
			total += d;
		}

		double averageSEA = total / projectSEA.size();

		for (Double d: projectEEA) {
			total2 += d;
		}

		double averageEEA = total2 / projectSEA.size();

		if (averageSEA < 1 && averageEEA < 1) {
			return 0;
		} else if (averageSEA < 1 && averageEEA >= 1) {
			return -1;
		} else if (averageEEA < 1 && averageSEA >= 1) {
			return 1;
		}

		return 100;
	}

	/**
	 * Predicts the due date of a project based off time spent on
	 * comleted Sprints as well as time spent on open sprints so far
	 * 
	 * @param project
	 * @return Date
	 * @throws IOException 
	 */
	public Date projectedDueDate (JiraProject project) throws IOException {
		Date dueDate = project.getDueDate();
		Calendar c = Calendar.getInstance();
		c.setTime(dueDate);
SprintServices sprintService=new SprintServices(mainClient, authorization, baseURL);

		int totalDifference = 0;
		int completedIssues = 0;

		for (Sprint s: project.getSprints()) {
			if (s.isClosed()) {
				System.out.println("Closed: " + s.getName());
				int durationDiff = SprintServices.sprintDifference(s);
				System.out.println("End: " + s.getEndDate());
				System.out.println("Completed: " + s.getCompleteDate());
				totalDifference += durationDiff;
			} else if (s.isOpen()) {
				System.out.print("Open: " + s.getName());
				System.out.println("End: " + s.getEndDate());
				List<JiraIssue> issueList = sprintService.getIssuesBySprint(s, mainClient);
				for (JiraIssue i: issueList) {
					if ("Closed".equals(i.getStatus()) ||
							"Resolved".equals(i.getStatus()) ||
							"Done".equals(i.getStatus())) {
						completedIssues++;
					}
				}
				int days = Days.daysBetween(new DateTime(s.getStartDate()), 
						new DateTime(Calendar.getInstance().getTime())).getDays();
				double openTotal = (double) (days / completedIssues);
				double extraDays = openTotal * SprintServices.estimateDays(s);
				double dayDiff = extraDays - days;
				totalDifference += dayDiff;
				completedIssues = 0;
			}
		}

		double overUnder = totalDifference / (project.sprintsWorked());
		double extraEstimate = overUnder * project.sprintsNotWorked();
		totalDifference = (int) Math.round(totalDifference + extraEstimate);
		c.add(Calendar.DATE, totalDifference);
		return c.getTime();
		//		Date dueDate = project.getDueDate();
		//		Calendar c = Calendar.getInstance();
		//		c.setTime(dueDate);
		//		int totalDifference = 0;
		//		int completedIssues = 0;
		//
		//
		//		for (Sprint s: project.getSprints()) {
		//			if(s.isClosed()) {
		//				int durationDiff = SprintServices.sprintDifference(s);
		//				System.out.format("This sprint was completed %d day(s) after behind schedule.\n", 
		//						durationDiff);
		//				totalDifference += durationDiff;
		//			} 
		//			
		//			if (s.isOpen()) {
		//				List<Issue> issuesForSprint = SprintServices.getIssuesBySprint(s, 
		//						mainClient);
		//				for(Issue i: issuesForSprint) {
		//
		//					//Older Jiras use "Resolved" and "Closed", newer ones have the status "Done"
		//					if ("Closed".equals(i.getStatus().getName()) ||
		//							"Resolved".equals(i.getStatus().getName()) ||
		//							"Done".equals(i.getStatus().getName())) {
		//						completedIssues++;
		//					}
		//				}
		//				int days = Days.daysBetween(new DateTime(s.getStartDate()), 
		//						new DateTime(Calendar.getInstance().getTime())).getDays();
		//				double openTotal = (double) (days / completedIssues);
		//				int extraDays = (int) openTotal * SprintServices.estimateDays(s);
		//				totalDifference += extraDays;
		//				completedIssues = 0;
		//			}
		//		}
		//		c.add(Calendar.DATE, totalDifference);
		//		return c.getTime();
	}




}
