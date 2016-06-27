package com.sgt.pmportal.services;

import java.util.ArrayList;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.util.concurrent.Promise;
import com.sgt.pmportal.domain.JiraIssue;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.Release;

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
	JiraRestClient mainClient;

	/**
	 * Constructor 
	 * @param client
	 */
	public ProjectServices(JiraRestClient client) {
		this.client = client.getProjectClient();
		mainClient = client;
	}

	/**
	 * converts projects of the JRJC Project type to a JiraProject
	 * 
	 * @return ArrayList<JiraProject>
	 */
	public ArrayList<JiraProject> getAllJiraProjects() {
		ArrayList<Project> list = getAllProjects();
		ArrayList<JiraProject> jiraProjects = new ArrayList<JiraProject>();
		for(Project p: list) {
			jiraProjects.add(toJiraProject(p));
		}
		return jiraProjects;
	}

	/**
	 * returns a list of the projects on the client
	 * 
	 * @return ArrayList<Project>
	 */
	private ArrayList<Project> getAllProjects() {
		ArrayList<Project> projectList = new ArrayList<Project>();
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
				return toJiraProject(p);
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
		JiraProject project = null;
		try{
			project=toJiraProject(mainClient.getProjectClient().getProject(key).claim());
		}catch(RestClientException projectnotfound){
		}
		return project;
	}

	/**
	 * Returns a JiraProject from a JRJC Project
	 * 
	 * @param p
	 * @return JiraProject
	 */
	public JiraProject toJiraProject(Project p) {
		JiraProject jp = new JiraProject(p.getName(), p.getKey(), p.getLead(), 
				p.getDescription(), versionsToRelease(p.getVersions()), p.getUri());
		populateIssues(jp);
		return jp;
	}

	/**
	 * Returns a release array from an iterable object of versions
	 * @param versions
	 * @return
	 */
	private ArrayList<Release> versionsToRelease(Iterable<Version> versions) {
		ArrayList<Release> releases = new ArrayList<Release>();
		for(Version v: versions) {
			releases.add(new Release(v.getName(), v.getId(), v.getReleaseDate(), v.getSelf()));
		}
		return releases;
	}

	/**
	 * Converts BasicIssues to Issues and then adds them to the
	 * JiraProject it belongs too.
	 * @param jp
	 */
	public void populateIssues(JiraProject jp) {
		Promise<SearchResult> result = mainClient.getSearchClient().searchJql(
				"project=" + jp.getKey(),1000,0);
		SearchResult issues = result.claim();

		IssueRestClient issueClient = mainClient.getIssueClient();
		
		for (BasicIssue i : issues.getIssues()) {
			Promise<Issue> issueProm = issueClient.getIssue(i.getKey());
			Issue curIssue = issueProm.claim();
			
			//the following try and catch methods will prevent null pointer exceptions
			String description = "";
			String assigneeName = "";
			String priority="";
			DateTime creationDate = null;
			DateTime dueDate = null;
			
			try {
				description = curIssue.getDescription();
			} catch(NullPointerException exception){}			
			try{
				
				assigneeName = curIssue.getAssignee().getDisplayName();
			} catch(NullPointerException exception){
				assigneeName = null;
			}
			try{
				priority=curIssue.getPriority().getName();
				
			}catch(NullPointerException exception){
			}
			
			try{
				creationDate=curIssue.getCreationDate();
			} catch(NullPointerException exception){}
			try{
				dueDate=curIssue.getDueDate();
			} catch(NullPointerException exception){}

			JiraIssue jiraIssue = new JiraIssue(curIssue.getKey(), 
					curIssue.getIssueType().getName(), priority,
					description, assigneeName, creationDate, dueDate);

			jp.addToIssues(jiraIssue);
		}
	}

}
