package com.sgt.pmportal.resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
@Path("/config")
public class ConfigResource {
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/save")
	//saves a series of credentials, sent as a JSON object, to a config file
	public String saveCredentials(String request) throws IOException{
		JSONObject requestObject=new JSONObject(request);
		String username=requestObject.getString("username");
		String password=requestObject.getString("password");
		String url=requestObject.getString("url");
		String email=requestObject.getString("email");
		String seaMin=requestObject.getString("seaMin");
		String seaMax=requestObject.getString("seaMax");
		String eeaMin=requestObject.getString("eeaMin");
		String eeaMax=requestObject.getString("eeaMax");
		String bugMax=requestObject.getString("bugMax");
		String fileString;
		File textFile;
		//check if user already exists, then write if not
		try{
			//Tomcat
			textFile=new File("webapps/pmportal/data/config.txt");
			fileString=new String(Files.readAllBytes(Paths.get("webapps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
		}catch (Exception e){
			//glassfish
			try{
				textFile=new File("../applications/pmportal/data/config.txt");
				fileString=new String(Files.readAllBytes(Paths.get("../applications/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			}catch(NoSuchFileException e2){
				textFile=new File("../eclipseApps/pmportal/data/config.txt");
				fileString=new String(Files.readAllBytes(Paths.get("../eclipseApps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			}
		}
		if (fileString.toLowerCase().contains(username.toLowerCase())){
			Writer fileWriter=new BufferedWriter(new FileWriter(textFile));
			int startIndex=fileString.indexOf(username);
			int length=fileString.substring(startIndex).indexOf(";") + 1;
			int finalIndex=startIndex+length;
			String newString=fileString.substring(0, startIndex) + fileString.substring(finalIndex);
			fileWriter.write(newString+username+","+password+","+email+","+url+"," + seaMin + "," + seaMax + "," + eeaMin +"," + eeaMax + "," +bugMax+";");
			fileWriter.close();
		}else{
			Writer fileWriter=new BufferedWriter(new FileWriter(textFile, true));
			fileWriter.write(username+","+password+","+email+","+url+"," + seaMin + "," + seaMax + "," + eeaMin +"," + eeaMax + "," +bugMax+";");
			fileWriter.close();
		};
		return "Saved";
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get")

	//Retrieves all the information in the config file as a JSON string except passwords
	public static String getAllCredentials() throws IOException{
		JSONObject responseObject=new JSONObject();
		JSONArray responseArray=new JSONArray();
		String fileString;
		try{
			//Tomcat
			fileString=new String(Files.readAllBytes(Paths.get("webapps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
		}catch (Exception e){
			try{
				//glassfish
				fileString=new String(Files.readAllBytes(Paths.get("../applications/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			}catch(NoSuchFileException e2){
				fileString=new String(Files.readAllBytes(Paths.get("../eclipseApps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			}
		}
		//convert to JSONObject for client to read
		String[] userArray=fileString.split(";");
		for (String user:userArray){
			String[] userData=user.split(",");
			if (userData.length>1){
				JSONObject tempObject=new JSONObject();
				tempObject.put("username", userData[0]);
				tempObject.put("email", userData[2]);
				tempObject.put("url", userData[3]);
				tempObject.put("seaMin", userData[4]);
				tempObject.put("seaMax", userData[5]);
				tempObject.put("eeaMin", userData[6]);
				tempObject.put("eeaMax", userData[7]);
				tempObject.put("bugMax", userData[8]);
				responseArray.put(tempObject);
			}
		}
		responseObject.put("users", responseArray);
		return responseObject.toString();
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get/user/{username}")
	//Retrieves information on an individual user
	public String getUserCredentials(@PathParam ("username") String username) throws IOException{
		JSONObject responseObject=new JSONObject();
		String fileString;
		try{
			//Tomcat
			fileString=new String(Files.readAllBytes(Paths.get("webapps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
		}catch (Exception e){
			try{
				//glassfish
				fileString=new String(Files.readAllBytes(Paths.get("../applications/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			}catch(NoSuchFileException e2){
				fileString=new String(Files.readAllBytes(Paths.get("../eclipseApps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			}
		}
		if (fileString.toLowerCase().contains(username.toLowerCase())){
			int startIndex=fileString.indexOf(username);
			//do not add 1 to length or else will include semicolon
			int length=fileString.substring(startIndex).indexOf(";");
			int finalIndex=startIndex+length;
			String userString=fileString.substring(startIndex, finalIndex);
			String[] userData=userString.split(",");
			responseObject.put("username", userData[0]);
			responseObject.put("email", userData[2]);
			responseObject.put("url", userData[3]);
			responseObject.put("seaMin", userData[4]);
			responseObject.put("seaMax", userData[5]);
			responseObject.put("eeaMin", userData[6]);
			responseObject.put("eeaMax", userData[7]);
			responseObject.put("bugMax", userData[8]);
		}
		return responseObject.toString();
	}

	//DO NOT EXPOSE TO REST
	public static JSONObject getUsersUnexposed() throws IOException{
		JSONObject responseObject=new JSONObject();
		JSONArray responseArray=new JSONArray();
		String fileString;
		try{
			//Tomcat
			fileString=new String(Files.readAllBytes(Paths.get("webapps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
		}catch (Exception e){
			//glassfish
			try{
				fileString=new String(Files.readAllBytes(Paths.get("../applications/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			}catch(NoSuchFileException e2){
				fileString=new String(Files.readAllBytes(Paths.get("../eclipseApps/pmportal/data/config.txt")), StandardCharsets.UTF_8);
			}
		}
		//convert to JSONObject for client to read
		String[] userArray=fileString.split(";");
		for (String user:userArray){
			String[] userData=user.split(",");
			if (userData.length>1){
				JSONObject tempObject=new JSONObject();
				tempObject.put("username", userData[0]);
				tempObject.put("password", userData[1]);
				tempObject.put("email", userData[2]);
				tempObject.put("url", userData[3]);
				tempObject.put("seaMin", userData[4]);
				tempObject.put("seaMax", userData[5]);
				tempObject.put("eeaMin", userData[6]);
				tempObject.put("eeaMax", userData[7]);
				tempObject.put("bugMax", userData[8]);
				responseArray.put(tempObject);
			}
		}
		responseObject.put("users", responseArray);
		return responseObject;
	}
}
