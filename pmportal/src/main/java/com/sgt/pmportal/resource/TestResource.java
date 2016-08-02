package com.sgt.pmportal.resource;
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
	@Path ("/login/{username}/{password}/{url:.+}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testLogin(@PathParam ("username") String username,
			@PathParam ("password")	String password,
			@PathParam ("url") String url){
		try{
			@SuppressWarnings("unused")
			JiraRestClient client=GeneralServices.login(url, username, password);
		}catch (Exception e){
			return "Failed";
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
