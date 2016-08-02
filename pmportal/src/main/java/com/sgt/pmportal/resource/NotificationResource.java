package com.sgt.pmportal.resource;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;


@Path("/notification")
public class NotificationResource {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCredentials(JSONObject requestObject){
		String username=requestObject.getString("username");
		String password=requestObject.getString("password");
		String url=requestObject.getString("url");
		String email=requestObject.getString("email");
			try{
		    	File textFile;
		    	try{
		    		//Tomcat
		    	textFile=new File("webapps/pmportal/data/notify.txt");
		    	}catch (Exception e){
		    		//glassfish
		    		textFile=new File("../docroot/data/notify.txt");
		    	}
		    	//The key is to split ";" then to split ","
		    	Writer fileWriter=new BufferedWriter(new FileWriter(textFile));
		    	fileWriter.write(username+","+password+","+email+","+url+";");
		    	fileWriter.close();
		    }catch (Exception e){
		    	e.printStackTrace();
		    }
	}
}
