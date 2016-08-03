package com.sgt.pmportal.resource;

import javax.ws.rs.Path;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.POST;


@Path("/notification")
public class NotificationResource {

	@POST
	@Path("/save")
	public String saveCredentials(String request) throws IOException{
		JSONObject requestObject=new JSONObject(request);
		String username=requestObject.getString("username");
		String password=requestObject.getString("password");
		String url=requestObject.getString("url");
		String email=requestObject.getString("email");
		//check if user already exists, then write if not

		try{
			//Tomcat
			File textFile=new File("webapps/pmportal/data/config.txt");
			//See if file exists, if it does, perform operations, or else, just write to it
				String fileString=new String(Files.readAllBytes(Paths.get("webapps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
				if (fileString.toLowerCase().contains(username.toLowerCase())){
					Writer fileWriter=new BufferedWriter(new FileWriter(textFile));
					int startIndex=fileString.indexOf(username);
					int length=fileString.substring(startIndex).indexOf(";") + 1;
					int finalIndex=startIndex+length;
					String newString=fileString.substring(0, startIndex) + fileString.substring(finalIndex);
					fileWriter.write(newString+username+","+password+","+email+","+url+";");
					fileWriter.close();
				}else{
					Writer fileWriter=new BufferedWriter(new FileWriter(textFile, true));
					fileWriter.write(username+","+password+","+email+","+url+";");
					fileWriter.close();
				};

		}catch (Exception e){
			//glassfish
			File textFile=new File("../applications/pmportal/data/config.txt");
			String fileString=new String(Files.readAllBytes(Paths.get("../applications/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			if (fileString.toLowerCase().contains(username.toLowerCase())){
				Writer fileWriter=new BufferedWriter(new FileWriter(textFile));
				int startIndex=fileString.indexOf(username);
				int length=fileString.substring(startIndex).indexOf(";") + 1;
				int finalIndex=startIndex+length;
				String newString=fileString.substring(0, startIndex) + fileString.substring(finalIndex);
				fileWriter.write(newString+username+","+password+","+email+","+url+";");
				fileWriter.write(username+","+password+","+email+","+url+";");
				fileWriter.close();
			}else{
				Writer fileWriter=new BufferedWriter(new FileWriter(textFile, true));
				fileWriter.write(username+","+password+","+email+","+url+";");
				fileWriter.close();
			};
		}
		return "Saved";
	}
}