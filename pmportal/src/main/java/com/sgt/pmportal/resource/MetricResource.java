package com.sgt.pmportal.resource;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
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
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.domain.JiraProject;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.MetricsServices;
import com.sgt.pmportal.services.ProjectServices;

@Path ("/metrics")
public class MetricResource {
	
	@Path("/project/basic/{projectKey}/{username}/{password}/{url:.+}")
	//serverURL/pmportal/rest/metrics/project/basic...
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
			@PathParam("url") String url) throws URISyntaxException, IOException, ParseException{
		JiraRestClient client=GeneralServices.login(url, username, password);
		String authorization=GeneralServices.encodeAuth(username, password);
		ProjectServices projectService=new ProjectServices(client, authorization, url);
		JiraProject project=projectService.getProjectByKey(key);
		MetricsServices metricService=new MetricsServices(client, authorization, url);
		List<List<Double>> dataList=metricService.predictNext(project);
		JSONObject responseObject=new JSONObject();
		responseObject.put("sea", dataList.get(0).toString());
		responseObject.put("eea", dataList.get(1).toString());
		responseObject.put("bugs", dataList.get(2).toString());
		//create string out of JSON object, convert it to DOM object, transform into file
		String xmlString=XML.toString(responseObject, "root");
		System.out.println(xmlString);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    DocumentBuilder builder;  
	    try  
	    {  
	        builder = factory.newDocumentBuilder();  
	        Document document = builder.parse( new InputSource( new StringReader( xmlString ) ) );  
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        Result output = new StreamResult(new File("output.xml"));
	        Source input = new DOMSource(document);
	        transformer.transform(input, output);
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    } 
		return responseObject.toString();
	}
	
}
