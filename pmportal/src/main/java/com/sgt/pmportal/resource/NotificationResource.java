package com.sgt.pmportal.resource;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;


@Path("/notification")
public class NotificationResource {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCredentials(JSONObject requestObject) throws IOException{
		String username=requestObject.getString("username");
		String password=requestObject.getString("password");
		String url=requestObject.getString("url");
		String email=requestObject.getString("email");
		//create excel file
				String lineBreak= System.getProperty("line.separator");
				File textFile;
				try{
					//Tomcat
					textFile=new File("webapps/pmportal/data/"+username+".txt");
					Writer fileWriter=new BufferedWriter(new FileWriter(textFile));
					fileWriter.write("SEA	EEA	Bugs");
					fileWriter.write(lineBreak);
					fileWriter.close();
				}catch (Exception e){
					//glassfish
					textFile=new File("../applications/pmportal/data/"+username+".txt");
					Writer fileWriter=new BufferedWriter(new FileWriter(textFile));
					fileWriter.write("SEA	EEA	Bugs");
					fileWriter.write(lineBreak);
					fileWriter.close();
				}
}
}