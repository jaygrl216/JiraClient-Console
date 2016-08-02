package com.sgt.pmportal.resource;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;


@Path("/notification")
public class NotificationResource {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCredentials(JSONObject requestObject){
		
	}
}
