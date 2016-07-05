package com.sgt.pmportal.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.RestClientException;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.Sprint;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.MetricsServices;
import com.sgt.pmportal.services.ProjectServices;
import com.sgt.pmportal.services.SprintServices;

public class MetricTest {
	private static final String JIRA_URL = "http://54.152.100.242/jira";
	private static final String JIRA_ADMIN_USERNAME = "amital";
	private static final String JIRA_ADMIN_PASSWORD = "ComPuteR90";
	private static String authorization;
	private static JiraRestClient client=login();
	ProjectServices pService=new ProjectServices(client, authorization, JIRA_URL);
	MetricsServices  metricService=new MetricsServices(client, authorization, JIRA_URL);
	SprintServices sprintService=new SprintServices(client, authorization, JIRA_URL);
	JiraProject project=pService.toJiraProject(client.getProjectClient().getProject("PA").claim(),null);
	List<Sprint> sprintList=new ArrayList<Sprint>();
	/**
	 * logs in to JiraClient
	 * @return JiraRestClient
	 */
	private static JiraRestClient login() {
		client = null;
		try {
			client = GeneralServices.login(JIRA_URL, JIRA_ADMIN_USERNAME, 
					JIRA_ADMIN_PASSWORD);
			authorization=GeneralServices.encodeAuth(JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);
		} catch (URISyntaxException e) {
			System.err.println("Cannot login!");
		}
		return client;
	}
	public static void main(String[] args) throws IOException, ParseException{
		MetricTest test=new MetricTest();
		test.printInfo();
		test.testProgress();
		test.testSprintSEA();
		test.testOverallSEA();
		test.testSprintEEA();
		test.testOverallEEA();
		test.testBugs();
		test.testAllDefects();
		test.testForecast();
		System.out.println("Finished");
	}
	public void printInfo() {
		System.out.println("******PM-Portal Metric test********\n");
		System.out.println("Jira server: " + JIRA_URL);
		System.out.println("Using login for: " + JIRA_ADMIN_USERNAME+"\n");
	}
	public void testProgress(){
		System.out.println("Progress test will display the progress of a project as a\n"
				+ "percentage.\n");
		try{
			double progress=metricService.calculateProgress(project.getKey());
			System.out.println("The progress on project "+project.getName()+" is: " + progress+"%");
		} catch (RestClientException noProject){
			System.err.println("Project does not exist");
		}
	}
	public void testSprintSEA() throws IOException, ParseException{
		System.out.println("Getting sprints...");
		try{
			sprintList=sprintService.getClosedSprintsByProject(project);
			Sprint sprint=sprintList.get(0);
			//MetricsService test
			System.out.println("SEA test will display the sea of a sprint as a\n"
					+ "percentage.\n");
			double sea=MetricsServices.calculateSprintSEA(sprint);
			System.out.println("The SEA of sprint "+sprint.getName()+" is: " + sea+"\n");
		}catch(IndexOutOfBoundsException noSprint){
			System.err.println(noSprint);
		}
	}
	public void testOverallSEA() throws IOException, ParseException{
		System.out.println("Overall SEA test will display the SEA of a project and its standard deviation");
		List<Double> seaMetric=metricService.calculateProjectSEA(project, null);
		System.out.println("SEA: "+ seaMetric.get(0) + "+/- " + seaMetric.get(1)+"\n");
	}
	public void testSprintEEA() throws IOException, ParseException{
		System.out.println("Sprint EEA test will display the EEA of a sprint");
		try{
			sprintList=sprintService.getClosedSprintsByProject(project);
			Sprint sprint=sprintList.get(0);
			System.out.println("EEA: "+ metricService.calculateSprintEEA(sprint)+"\n");
		}catch(IndexOutOfBoundsException noSprint){
			System.err.println(noSprint);
		}
	}
	public void testOverallEEA() throws IOException, ParseException{
		System.out.println("Overall EEA test will display the EEA of a project and its standard deviation");
		List<Double> eeaMetric=metricService.calculateProjectEEA(project, null);
		System.out.println("EEA: "+ eeaMetric.get(0) + "+/- " + eeaMetric.get(1)+"\n");
	}
	public void testBugs() {
		System.out.println("Bug test will display number of bugs in a project");
		double bugNum=metricService.calculateBugs(project.getKey());
		System.out.println("Bugs: "+bugNum+"\n");
	}
	public void testAllDefects() throws IOException, ParseException{
		System.out.println("All defects test will calculate all metrics and find defects for a project");
		List<Number> defectArray=metricService.calculateDefectTotal(project);
		System.out.println("Defects");
		System.out.println("Bugs: "+defectArray.get(0));
		System.out.println("SEA: "+defectArray.get(1));
		System.out.println("EEA: "+defectArray.get(2));
		System.out.println("Overdue: "+defectArray.get(3)+"\n");
	}
	public void testForecast() throws JSONException, IOException, ParseException {
		System.out.println("Forecast test will display SEA, EEA, and bug values per sprint with predicted next values");
		List<List<Double>> dataList=metricService.predictNext(project);
		if (dataList.size()>0){
			List<Double >seaList=dataList.get(0);
			List<Double> eeaList=dataList.get(1);
			List<Double> bugList=dataList.get(2);
			double seaSlope=metricService.getRegressionSlope(seaList);
			double eeaSlope=metricService.getRegressionSlope(eeaList);
			double bugSlope=metricService.getRegressionSlope(bugList);
			List<Double> seaForecast=metricService.getForecastInterval(seaList, seaSlope);
			List<Double> eeaForecast=metricService.getForecastInterval(eeaList, eeaSlope);
			List<Double> bugForecast=metricService.getForecastInterval(bugList, bugSlope);
			System.out.println("SEA values:");
			for (int i=0; i<seaList.size(); i++){
				System.out.print("\nSprint: " +(i+1)+", SEA: "+seaList.get(i));
			}
			System.out.print("+- "+seaForecast.get(0)+" <--- Predicted value with forecast interval\n");
			System.out.println("Error on regression: " + seaForecast.get(1)+"\n\nEEA values:");
			for (int i=0; i<eeaList.size(); i++){
				System.out.print("\nSprint: " +(i+1)+", EEA: "+eeaList.get(i));
			}
			System.out.print("+- "+eeaForecast.get(0)+ " <--- Predicted value with forecast interval\n");
			System.out.println("Error on regression: "+eeaForecast.get(1)+"\n\nBug values:");
			for (int i=0; i<bugList.size(); i++){
				System.out.print("\nSprint: " +(i+1)+", Bugs: "+bugList.get(i));
			}
			System.out.print("+- "+bugForecast.get(0)+ " <--- Predicted value with forecast interval\n");
			System.out.println("Error on regression: "+bugForecast.get(1));
		} else{
			System.err.println("No data available!");
		}
	}
}