package com.sgt.pmportal.resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
@Path("/config")
public class ConfigResource {
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
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get")
	public String getAllCredentials() throws IOException{
		JSONObject responseObject=new JSONObject();
		JSONArray responseArray=new JSONArray();
		try{
			//Tomcat
			//read file
			String fileString=new String(Files.readAllBytes(Paths.get("webapps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			//convert to JSON so it can be easily manipulated client-side
			String[] userArray=fileString.split(";");

			for (String user:userArray){
				String[] userData=user.split(",");
				if (userData.length>1){
					JSONObject tempObject=new JSONObject();
					tempObject.put("username", userData[0]);
					tempObject.put("email", userData[2]);
					tempObject.put("url", userData[3]);
					responseArray.put(tempObject);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			//glassfish
			String fileString=new String(Files.readAllBytes(Paths.get("../applications/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			String[] userArray=fileString.split(";");

			for (String user:userArray){
				String[] userData=user.split(",");
				if (userData.length>1){
					JSONObject tempObject=new JSONObject();
					tempObject.put("username", userData[0]);
					tempObject.put("email", userData[2]);
					tempObject.put("url", userData[3]);
					responseArray.put(tempObject);
				}
			}
		}
		responseObject.put("users", responseArray);
		return responseObject.toString();
	}
}
