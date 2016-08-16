package com.sgt.pmportal.resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.domain.Sprint;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.MetricsServices;
import com.sgt.pmportal.services.ProjectServices;
import com.sgt.pmportal.services.SprintServices;

@Path ("/metrics")
public class MetricResource {

	@Path("/project/basic/{projectKey}/{username}/{password}/{url:.+}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBasicMetrics(@PathParam ("projectKey") String key, 
			@PathParam ("username") String username, 
			@PathParam ("password") String password, 
			@PathParam("url") String url) throws URISyntaxException, IOException, ParseException{
		JiraRestClient client=GeneralServices.login(url, username, password);
		String authorization=GeneralServices.encodeAuth(username, password);
		ProjectServices projectService=new ProjectServices(client, authorization, url);
		JiraProject project=projectService.getProjectByKey(key);
		MetricsServices metricService=new MetricsServices(client, authorization, url);
		List<Number> defectList=metricService.calculateDefectTotal(project);
		Date projectedDate = projectService.projectedDueDate(project);
		Double progress=metricService.calculateProgress(key);
		String responseString="{\"bugs\":\"" + defectList.get(0) +
				"\", \"sea\":\"" + defectList.get(1) + 
				"\", \"eea\":\"" + defectList.get(2) + 
				"\", \"overdue\":\"" + defectList.get(3) +
				"\", \"endDate\":\"" + project.getDueDateString() +
				"\", \"projectedDate\":\"" + projectedDate.toString() +
				"\", \"progress\":\"" + progress.toString()	+ "\"}";
		return responseString;
	}

	@Path("/project/detail/{projectKey}/{username}/{password}/{url:.+}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getDetailMetrics(@PathParam ("projectKey") String key, 
			@PathParam ("username") String username, 
			@PathParam ("password") String password, 
			@PathParam("url") String url) throws URISyntaxException, IOException, ParseException, TransformerException, ParserConfigurationException, SAXException{
		JiraRestClient client=GeneralServices.login(url, username, password);
		String authorization=GeneralServices.encodeAuth(username, password);
		ProjectServices projectService=new ProjectServices(client, authorization, url);
		JiraProject project=projectService.getProjectByKey(key);
		MetricsServices metricService=new MetricsServices(client, authorization, url);
		List<List<Double>> dataList=metricService.predictNext(project);
		JSONObject responseObject=new JSONObject();
		//In case data is too small
		if (dataList.size()==0){
			responseObject.put("response", 0);
			return responseObject.toString();
		};
		responseObject.put("sea", dataList.get(0).toString());
		responseObject.put("eea", dataList.get(1).toString());
		responseObject.put("bugs", dataList.get(2).toString());
		//create XML out of JSON object
		String xmlString=XML.toString(responseObject, key);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document document = builder.parse( new InputSource( new StringReader( xmlString ) ) );
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output;
		Source input = new DOMSource(document);
		try{
			//Tomcat
			output = new StreamResult(new File("webapps/pmportal/data/output.xml"));
			transformer.transform(input, output);
		}catch (Exception e){
			try{
			output = new StreamResult(new File("../applications/pmportal/data/output.xml"));
			transformer.transform(input, output);
			}catch(Exception e2){
				output = new StreamResult(new File("../eclipseApps/pmportal/data/output.xml"));
				transformer.transform(input, output);
			}
		}
		//create excel file
		String lineBreak= System.getProperty("line.separator");
		File textFile;
		Writer fileWriter;
		try{
			//Tomcat
			textFile=new File("webapps/pmportal/data/output.xls");
			fileWriter=new BufferedWriter(new FileWriter(textFile));
		}catch (Exception e){
			//glassfish
			try{
			textFile=new File("../applications/pmportal/data/output.xls");
			fileWriter=new BufferedWriter(new FileWriter(textFile));
			}catch(Exception e2){
				//glassfish eclipse
				textFile=new File("../eclipseApps/pmportal/data/output.xls");
				fileWriter=new BufferedWriter(new FileWriter(textFile));
			}
		}
		fileWriter.write("SEA	EEA	Bugs");
		fileWriter.write(lineBreak);
		for (int i=0; i<dataList.get(0).size(); i++){
			fileWriter.write(dataList.get(0).get(i).toString() +"	"+ dataList.get(1).get(i).toString()+"	"+dataList.get(2).get(i).toString());
			fileWriter.write(lineBreak);
		}
		fileWriter.close();
		responseObject.put("response", 200);
		return responseObject.toString();
	}

	@Path("/all/{username}/{password}/{url:.+}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllMetrics(@PathParam ("username") String username,
			@PathParam ("password") String password,
			@PathParam("url") String url) throws URISyntaxException, IOException, ParseException{
		JiraRestClient client=GeneralServices.login(url, username, password);
		String authorization=GeneralServices.encodeAuth(username, password);
		ProjectServices projectService=new ProjectServices(client, authorization, url);
		List<JiraProject> projectList=projectService.getAllJiraProjects();
		MetricsServices metricService=new MetricsServices(client, authorization, url);
		SprintServices sprintService=new SprintServices(client, authorization, url);
		JSONObject responseObject=new JSONObject();
		JSONArray projectArray=new JSONArray();
		for (JiraProject project:projectList){
			String key=project.getKey();
			String name=project.getName();
			List<Sprint> sprintList=sprintService.getClosedSprintsByProject(project);
			Double sea=metricService.calculateProjectSEA(project, sprintList);
			Double eea=metricService.calculateProjectEEA(project, sprintList);
			Long bugs=metricService.calculateBugs(key);
			Double progress=metricService.calculateProgress(key);
			String projectString="{\"name\":\""+name+"\", \"bugs\":\"" + bugs +
					"\", \"sea\":\"" + sea +
					"\", \"eea\":\"" + eea +
					"\", \"progress\":\"" + progress.toString()	+ "\"}";
			JSONObject projectObject=new JSONObject(projectString);
			projectArray.put(projectObject);
		}
		responseObject.put("project", projectArray);
		return responseObject.toString();
	}
	@Path("/all/projects/{username}/{password}/{url:.+}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getProjectNames(@PathParam ("username") String username,
			@PathParam ("password") String password,
			@PathParam("url") String url) throws URISyntaxException, IOException, ParseException{
		JiraRestClient client=GeneralServices.login(url, username, password);
		Iterable<BasicProject> projectList=client.getProjectClient().getAllProjects().claim();
		JSONObject responseObject=new JSONObject();
		JSONArray projectArray=new JSONArray();
		for (BasicProject project:projectList){
			JSONObject tempObject=new JSONObject();
			tempObject.put("name", project.getName());
			tempObject.put("key", project.getKey());
			projectArray.put(tempObject);
		}
		responseObject.put("projects", projectArray);
		return responseObject.toString();
	}
	@Path("/averages/{username}/{password}/{url:.+}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//This method takes a really long time
	public String getAverages(@PathParam ("username") String username,
			@PathParam ("password") String password,
			@PathParam("url") String url) throws URISyntaxException, IOException, ParseException{
		JiraRestClient client=GeneralServices.login(url, username, password);
		String authorization=GeneralServices.encodeAuth(username, password);
		MetricsServices metricService=new MetricsServices(client, authorization, url);
		JSONObject responseObject=new JSONObject();
		List<Double> averages = metricService.getAverageSEAAndEEA();
		
		responseObject.put("aveSEA", averages.get(0));
		responseObject.put("aveEEA", averages.get(1));
		return responseObject.toString();
	}
	
	
}
