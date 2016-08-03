package com.sgt.pmportal.resource;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;


@Path("/notification")
public class NotificationResource {
	@Path("/save")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCredentials(JSONObject requestObject) throws IOException{
		String username=requestObject.getString("username");
		String password=requestObject.getString("password");
		String url=requestObject.getString("url");
		String email=requestObject.getString("email");
		//check if user already exists, then write if not
		
				try{
					//Tomcat
					File textFile=new File("webapps/pmportal/data/config.txt");
					String fileString=new String(Files.readAllBytes(Paths.get("webapps/pmportal/data/notify.txt")));
					String[] fileArray=fileString.split(";");
					for (String user:fileArray){
						String[] userData=user.split(",");
						for (String data:userData){
							if (data.equalsIgnoreCase(username)){
								
							}
						}
					};
					Writer fileWriter=new BufferedWriter(new FileWriter(textFile, true));
					fileWriter.write(username+","+password+","+email+","+url+";");
					fileWriter.close();
				}catch (Exception e){
					//glassfish
					File textFile=new File("../applications/pmportal/data/config.txt");
					Writer fileWriter=new BufferedWriter(new FileWriter(textFile, true));
					fileWriter.write(username+","+password+","+email+","+url+";");
					fileWriter.close();
				}
}
}