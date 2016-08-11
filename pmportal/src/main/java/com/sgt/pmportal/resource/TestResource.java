package com.sgt.pmportal.resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.sgt.pmportal.services.GeneralServices;
import com.sgt.pmportal.services.NotificationService;
@Path("/test")
public class TestResource {
	@Path ("/login/{pm}/{password}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testLogin(@PathParam ("pm") String pm,
			@PathParam ("password")	String password) throws IOException{
		File configFile=new File("config.txt");
		if (configFile.exists()){
		String fileString=new String(Files.readAllBytes(Paths.get("config.txt")), StandardCharsets.UTF_8);
		if (fileString.toLowerCase().contains(pm.toLowerCase())){
			int startIndex=fileString.indexOf(pm);
			int length=fileString.substring(startIndex).indexOf(";");
			int finalIndex=startIndex+length;
			String pmString=fileString.substring(startIndex, finalIndex);
			String[] pmData=pmString.split(",");
			if (password.equals(pmData[1])){
				return "Success";
			};
		}
		};
		return "Fail";
	}
	@Path ("/jira/{username}/{password}/{url:.+}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testJira(@PathParam ("username") String username,
			@PathParam ("password")	String password, @PathParam("url") String url) throws IOException{
		try{
			JiraRestClient client=GeneralServices.login(url, username, password);
			client.getMetadataClient().getServerInfo().claim();
		}catch(Exception e){
			return "Fail";
		}
		return "Success";
	}

	@Path ("/email/{address}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testEmail(@PathParam ("address") String m_to){
		try{
			String m_subject="Test of PM-Portal Alert System";
			String m_text="Hello, this message is to let you know that the PM-Portal notification system was able to send to your email.";
			@SuppressWarnings("unused")
			NotificationService nService=new NotificationService(m_to, m_subject, m_text);
		}catch (Exception e){
			return "Failed";
		}
		return "Sent";
	}
}
