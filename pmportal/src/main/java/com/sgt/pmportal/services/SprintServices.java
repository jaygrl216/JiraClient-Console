package com.sgt.pmportal.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.Sprint;

public class SprintServices {
	JiraRestClient client;
	String authorization;
	String baseURL;
	
	public SprintServices(JiraRestClient client, String authorization, String baseURL){
		this.client = client;
		this.authorization = authorization;
		this.baseURL = baseURL;
	}
	
	public ArrayList<Sprint> getOpenSprintsByProject(JiraProject project) 
			throws IOException, JSONException, ParseException{
		String boardId = "0";
		ArrayList<Sprint> sprintList = new ArrayList<Sprint>();
		JSONArray boards = new JSONArray();
		
		try{
			String boardResponse = getAgileData("/rest/agile/latest/board");
			JSONObject boardObject = new JSONObject(boardResponse);
			boards = boardObject.getJSONArray("values");
			
			for (int i = 0; i < boards.length(); i++){
				if (Objects.equals(boards.getJSONObject(i).get(
						"name").toString(), project.getKey()+" board")){
					boardId = boards.getJSONObject(i).get("id").toString();
					i = boards.length();
				}
			}	
			
		} catch (FileNotFoundException fileException){
			System.err.println("Warning: Version of Jira is outdated! "
					+ "Attempting to fix with Greenhopper API");
			
			String boardResponse = getAgileData("/rest/greenhopper/latest/rapidview");
			JSONObject boardObject=new JSONObject(boardResponse);
			boards = boardObject.getJSONArray("views");
			for (int i = 0; i < boards.length(); i++){
				if (Objects.equals(boards.getJSONObject(i).get("name").toString(), 
						project.getName())){
					boardId = boards.getJSONObject(i).get("id").toString();
					i = boards.length();
				}
			}
		}
		if (boardId!= "0"){
		try {
			JSONArray sprintArray=new JSONArray();
			String sprintResponse=getAgileData("/rest/agile/latest/board/" + boardId + "/sprint?state=active");
			if (sprintResponse==null){
				return sprintList;
			}
			JSONObject sprintObject=new JSONObject(sprintResponse);
			sprintArray=sprintObject.getJSONArray("values");
			//The data comes in in different formats. To simplify, we will convert them both to simple date objects
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);	
			//retrieve data
			for (int i=0; i<sprintArray.length(); i++){
				JSONObject iteratorObject=sprintArray.getJSONObject(i);
				sprintList.add(new Sprint(iteratorObject.get("name").toString(), iteratorObject.get("id").toString(), 
						iteratorObject.get("state").toString(), format.parse(iteratorObject.get("startDate").toString()), 
						format.parse(iteratorObject.get("endDate").toString()), null));
			}
		}catch(FileNotFoundException fException2){
			//greenhopper sprint call			
			String sprintGreenHopperResponse=getAgileData("/rest/greenhopper/latest/sprintquery/" + boardId);
			JSONObject sprintGreenHopperObject=new JSONObject(sprintGreenHopperResponse);
			JSONArray sprintGreenHopperArray=sprintGreenHopperObject.getJSONArray("sprints");
			ArrayList<String> idArray=new ArrayList<String>();
			ArrayList<JSONObject> sprintArray=new ArrayList<JSONObject>();
			//format date
			SimpleDateFormat oldFormat=new SimpleDateFormat("dd/MMM/yy hh:mm a", Locale.ENGLISH);
			//filter out closed and future sprints
			for (int i=0; i<sprintGreenHopperArray.length(); i++){
				JSONObject iteratorObject=sprintGreenHopperArray.getJSONObject(i);
				if (Objects.equals(iteratorObject.get("state").toString(), "ACTIVE")){
					idArray.add(iteratorObject.get("id").toString());
				}
			}
			//get each sprint and store it in an array
			for (String ia:idArray){
				String sprintResponse=getAgileData("/rest/greenhopper/latest/rapid/charts/sprintreport?rapidViewId="+boardId+"&sprintId="+ia);
				if (sprintResponse==null){
					return sprintList;
				}
				JSONObject responseObject=new JSONObject(sprintResponse);
				sprintArray.add(responseObject.getJSONObject("sprint"));
			}
			//get the data
			for (JSONObject sa:sprintArray){
				sprintList.add(new Sprint(sa.get("name").toString(), sa.get("id").toString(), 
						"active", oldFormat.parse(sa.get("startDate").toString()),
						oldFormat.parse(sa.getString("endDate").toString()), null));
			}

		}		
		}
		return sprintList;
	}
	public ArrayList<Sprint> getClosedSprintsByProject(JiraProject project) throws IOException, JSONException, ParseException{
		String boardId="0";
		ArrayList<Sprint> sprintList=new ArrayList<Sprint>();
		JSONArray boards=new JSONArray();
		try{
			String boardResponse=getAgileData("/rest/agile/latest/board");
			JSONObject boardObject=new JSONObject(boardResponse);
			boards=boardObject.getJSONArray("values");
			for (int i=0; i<boards.length(); i++){
				if (Objects.equals(boards.getJSONObject(i).get("name").toString(), project.getKey()+" board")){
					boardId=boards.getJSONObject(i).get("id").toString();
					i=boards.length();
				}
			}		
		}catch (FileNotFoundException fileException){
			System.err.println("Warning: Version of Jira is outdated! Attempting to fix with Greenhopper API");
			String boardResponse=getAgileData("/rest/greenhopper/latest/rapidview");
			JSONObject boardObject=new JSONObject(boardResponse);
			boards=boardObject.getJSONArray("views");
			for (int i=0; i<boards.length(); i++){
				if (Objects.equals(boards.getJSONObject(i).get("name").toString(), project.getName())){
					boardId=boards.getJSONObject(i).get("id").toString();
					i=boards.length();
				}
			}
		}
		if (boardId!="0"){
		try{
			JSONArray sprintArray=new JSONArray();
			String sprintResponse=getAgileData("/rest/agile/latest/board/" + boardId + "/sprint?state=closed");
			if (sprintResponse==null){
				return sprintList;
			}
			JSONObject sprintObject=new JSONObject(sprintResponse);
			sprintArray=sprintObject.getJSONArray("values");
			//format date
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
			//retrieve data
			for (int i=0; i<sprintArray.length(); i++){
				JSONObject iteratorObject=sprintArray.getJSONObject(i);
				sprintList.add(new Sprint(iteratorObject.get("name").toString(), iteratorObject.get("id").toString(), 
						iteratorObject.get("state").toString(), format.parse(iteratorObject.get("startDate").toString()), 
						format.parse(iteratorObject.get("endDate").toString()), format.parse(iteratorObject.get("completeDate").toString())));
			}
		}catch(FileNotFoundException fException2){
			//greenhopper sprint call			
			String sprintGreenHopperResponse=getAgileData("/rest/greenhopper/latest/sprintquery/" + boardId);
			JSONObject sprintGreenHopperObject=new JSONObject(sprintGreenHopperResponse);
			JSONArray sprintGreenHopperArray=sprintGreenHopperObject.getJSONArray("sprints");
			ArrayList<String> idArray=new ArrayList<String>();
			ArrayList<JSONObject> sprintArray=new ArrayList<JSONObject>();
			//format date
			SimpleDateFormat oldFormat=new SimpleDateFormat("dd/MMM/yy hh:mm a", Locale.ENGLISH);
			//filter out closed and future sprints
			for (int i=0; i<sprintGreenHopperArray.length(); i++){
				JSONObject iteratorObject=sprintGreenHopperArray.getJSONObject(i);
				if (Objects.equals(iteratorObject.get("state").toString(), "CLOSED")){
					idArray.add(iteratorObject.get("id").toString());
				}
			}
			//get each sprint and store it in an array
			for (String ia:idArray){
				String sprintResponse=getAgileData("/rest/greenhopper/latest/rapid/charts/sprintreport?rapidViewId="+boardId+"&sprintId="+ia);
				if (sprintResponse==null){
					return sprintList;
				}
				JSONObject responseObject=new JSONObject(sprintResponse);
				sprintArray.add(responseObject.getJSONObject("sprint"));
			}
			//get the data
			for (JSONObject sa:sprintArray){
				sprintList.add(new Sprint(sa.get("name").toString(), sa.get("id").toString(), 
						"closed", oldFormat.parse(sa.get("startDate").toString()),
						oldFormat.parse(sa.get("endDate").toString()), oldFormat.parse(sa.get("completeDate").toString())));
			}

		}
		}
		return sprintList;
	}
	public ArrayList<Sprint> getFutureSprintsByProject(JiraProject project) throws IOException{
		String boardId="0";
		ArrayList<Sprint> sprintList=new ArrayList<Sprint>();
		JSONArray boards=new JSONArray();
		try{
			String boardResponse=getAgileData("/rest/agile/latest/board");
			JSONObject boardObject=new JSONObject(boardResponse);
			boards=boardObject.getJSONArray("values");
			for (int i=0; i<boards.length(); i++){
				if (Objects.equals(boards.getJSONObject(i).get("name").toString(), project.getKey()+" board")){
					boardId=boards.getJSONObject(i).get("id").toString();
					i=boards.length();
				}
			}
		}catch (FileNotFoundException fileException){
			System.err.println("Warning: Version of Jira is outdated! Attempting to fix with Greenhopper API");
			String boardResponse=getAgileData("/rest/greenhopper/latest/rapidview");
			JSONObject boardObject=new JSONObject(boardResponse);
			boards=boardObject.getJSONArray("views");
			for (int i=0; i<boards.length(); i++){
				if (Objects.equals(boards.getJSONObject(i).get("name").toString(), project.getName())){
					boardId=boards.getJSONObject(i).get("id").toString();
					i=boards.length();
				}
			}
		}
		if (boardId!="0"){
		try{
			JSONArray sprintArray=new JSONArray();
			String sprintResponse=getAgileData("/rest/agile/latest/board/" + boardId + "/sprint?state=future");
			if (sprintResponse==null){
				return sprintList;
			}
			JSONObject sprintObject=new JSONObject(sprintResponse);
			sprintArray=sprintObject.getJSONArray("values");
			//format date not applicable to future sprints
			//retrieve data
			for (int i=0; i<sprintArray.length(); i++){
				JSONObject iteratorObject=sprintArray.getJSONObject(i);
				sprintList.add(new Sprint(iteratorObject.get("name").toString(), iteratorObject.get("id").toString(), 
						iteratorObject.get("state").toString(), null, null, null));
			}
		}catch(FileNotFoundException fException2){
			//greenhopper sprint call			
			String sprintGreenHopperResponse=getAgileData("/rest/greenhopper/latest/sprintquery/" + boardId);
			JSONObject sprintGreenHopperObject=new JSONObject(sprintGreenHopperResponse);
			JSONArray sprintGreenHopperArray=sprintGreenHopperObject.getJSONArray("sprints");
			ArrayList<String> idArray=new ArrayList<String>();
			ArrayList<JSONObject> sprintArray=new ArrayList<JSONObject>();
			//format date not used in future sprints
			//filter out closed and open sprints
			for (int i=0; i<sprintGreenHopperArray.length(); i++){
				JSONObject iteratorObject=sprintGreenHopperArray.getJSONObject(i);
				if (Objects.equals(iteratorObject.get("state").toString(), "FUTURE")){
					idArray.add(iteratorObject.get("id").toString());
				}
			}
			//get each sprint and store it in an array
			for (String ia:idArray){
				String sprintResponse=getAgileData("/rest/greenhopper/latest/rapid/charts/sprintreport?rapidViewId="+boardId+"&sprintId="+ia);
				if (sprintResponse==null){
					return sprintList;
				}
				JSONObject responseObject=new JSONObject(sprintResponse);
				sprintArray.add(responseObject.getJSONObject("sprint"));
			}
			//get the data
			for (JSONObject sa:sprintArray){
				sprintList.add(new Sprint(sa.get("name").toString(), sa.get("id").toString(), 
						"future", null,
						null, null));
			}

		}		
		}
		return sprintList;	
	}
	public ArrayList<Issue> getIssuesBySprint(Sprint sprint){
		ArrayList<Issue> issueList=new ArrayList<Issue>();
		Iterable<BasicIssue> sprintIssueList=client.getSearchClient().searchJql(
				"sprint= " + sprint.getId(),1000,0).claim().getIssues();
		System.out.print("Gathering Issues");
		for (BasicIssue sil:sprintIssueList){
			System.out.print(".");
			issueList.add(client.getIssueClient().getIssue(sil.getKey()).claim());
		}
		return issueList;

	}
	
	public String getAgileData(String url) throws IOException{
		StringBuffer response = new StringBuffer();
		URL urlObj = new URL(baseURL + url);
		HttpURLConnection connection= (HttpURLConnection) urlObj.openConnection();
		
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Authorization", "Basic " + authorization);
		connection.setRequestProperty("Content-type", "application/json");
		if (connection.getResponseCode()!=400){
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			response.append(line);
		}
		rd.close();
		return response.toString();
		}else{
			System.err.println("Project is not setup properly for agile");
		}return null;
	}
}