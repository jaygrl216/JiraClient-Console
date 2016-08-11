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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
@Path("/config")
public class ConfigResource {

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/save/new")
	//saves a series of credentials, sent as a JSON object, to a config file
	public String saveNew(String request) throws IOException{
		JSONObject requestObject=new JSONObject(request);
		String pm=requestObject.getString("pm");
		String password=requestObject.getString("password");
		String email=requestObject.getString("email");
		File textFile=new File("config.txt");
		textFile.createNewFile();
		Writer fileWriter=new BufferedWriter(new FileWriter(textFile, true));
		fileWriter.write(pm+","+password+","+email+";");
		fileWriter.close();
		System.out.println("Saved");
		return "Saved";
	}
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/save/update")
	//saves credentials, sent as a JSON object, to a config file
	public String saveEmail(String request) throws IOException{
		JSONObject requestObject=new JSONObject(request);
		String pm=requestObject.getString("pm");
		String password=requestObject.getString("password");
		String email=requestObject.getString("email");
		File textFile=new File("config.txt");
		String fileString=new String(Files.readAllBytes(Paths.get("config.txt")), StandardCharsets.UTF_8);
		Writer fileWriter=new BufferedWriter(new FileWriter(textFile));
		int startIndex=fileString.indexOf(pm);
		int length=fileString.substring(startIndex).indexOf(";") + 1;
		int finalIndex=startIndex+length;
		String newString=fileString.substring(0, startIndex) + fileString.substring(finalIndex);
		fileWriter.write(newString+pm+","+password+","+email+";");
		fileWriter.close();
		System.out.println("Saved");
		return "Saved";
	}


	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/save")
	//saves a series of credentials, sent as a JSON object, to a user config file
	public String saveCredentials(String request) throws IOException{
		JSONObject requestObject=new JSONObject(request);
		String pm=requestObject.getString("pm");
		String username=requestObject.getString("username");
		String password=requestObject.getString("password");
		String seaMin=requestObject.getString("seaMin");
		String seaMax=requestObject.getString("seaMax");
		String eeaMin=requestObject.getString("eeaMin");
		String eeaMax=requestObject.getString("eeaMax");
		String bugMax=requestObject.getString("bugMax");
		String url=requestObject.getString("url");
		String alias=requestObject.getString("alias");
		//check if user already exists, then write if not
	File textFile=new File(pm+".txt");
		textFile.createNewFile();
		String fileString=new String(Files.readAllBytes(Paths.get(pm+".txt")), StandardCharsets.UTF_8);
		if (fileString.toLowerCase().contains(username.toLowerCase())){
			Writer fileWriter=new BufferedWriter(new FileWriter(textFile));
			int startIndex=fileString.indexOf(username);
			int length=fileString.substring(startIndex).indexOf(";") + 1;
			int finalIndex=startIndex+length;
			String newString=fileString.substring(0, startIndex) + fileString.substring(finalIndex);
			fileWriter.write(newString+username+","+password+","+url+","+alias+","+ seaMin + "," + seaMax + "," + eeaMin +"," + eeaMax + "," +bugMax+";");
			fileWriter.close();
		}else{
			Writer fileWriter=new BufferedWriter(new FileWriter(textFile, true));
			fileWriter.write(username+","+password+","+url+","+alias+","+ seaMin + "," + seaMax + "," + eeaMin +"," + eeaMax + "," +bugMax+";");
			fileWriter.close();
		};
		System.out.println("Saved");
		return "Saved";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get/all/{pm}")
	//Retrieves all the users in a pm's file as a JSON string
	public static String getAllCredentials(@PathParam ("pm") String pm) throws IOException{
		JSONObject responseObject=new JSONObject();
		JSONArray responseArray=new JSONArray();
		String userString=new String(Files.readAllBytes(Paths.get(pm+".txt")), StandardCharsets.UTF_8);
		String[] userArray=userString.split(";");
		for (String user:userArray){
			String[] userData=user.split(",");
			if (userData.length>1){
				JSONObject tempObject=new JSONObject();
				tempObject.put("username", userData[0]);
				tempObject.put("alias", userData[3]);
				responseArray.put(tempObject);
			}
		}
		responseObject.put("users", responseArray);
		return responseObject.toString();
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get/user/{pm}/{username}")
	//Retrieves information on an individual user
	public String getUser(@PathParam ("pm") String pm, @PathParam("username") String username) throws IOException{
		JSONObject responseObject=new JSONObject();
		String email="";
		String pmString=new String(Files.readAllBytes(Paths.get("config.txt")), StandardCharsets.UTF_8);
		if (pmString.toLowerCase().contains(pm.toLowerCase())){
			int startIndex=pmString.indexOf(pm);
			int length=pmString.substring(startIndex).indexOf(";");
			int finalIndex=startIndex+length;
			String pmUserString=pmString.substring(startIndex, finalIndex);
			String[] pmData=pmUserString.split(",");
			email=pmData[0];
		}else{
			return responseObject.toString();
		}
		String fileString=new String(Files.readAllBytes(Paths.get(pm+".txt")), StandardCharsets.UTF_8);
		if (fileString.toLowerCase().contains(username.toLowerCase())){
			int startIndex=fileString.indexOf(username);
			//do not add 1 to length or else will include semicolon
			int length=fileString.substring(startIndex).indexOf(";");
			int finalIndex=startIndex+length;
			String userString=fileString.substring(startIndex, finalIndex);
			String[] userData=userString.split(",");
			responseObject.put("username", userData[0]);
			responseObject.put("email", email);
			responseObject.put("url", userData[2]);
			responseObject.put("alias", userData[3]);
			responseObject.put("seaMin", userData[4]);
			responseObject.put("seaMax", userData[5]);
			responseObject.put("eeaMin", userData[6]);
			responseObject.put("eeaMax", userData[7]);
			responseObject.put("bugMax", userData[8]);
		}
		return responseObject.toString();
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get/pm/{pm}")
	//Retrieves information on an individual user
	public String getPM(@PathParam ("pm") String pm) throws IOException{
		JSONObject responseObject=new JSONObject();
		String fileString=new String(Files.readAllBytes(Paths.get("config.txt")), StandardCharsets.UTF_8);
		if (fileString.toLowerCase().contains(pm.toLowerCase())){
			int startIndex=fileString.indexOf(pm);
			int length=fileString.substring(startIndex).indexOf(";");
			int finalIndex=startIndex+length;
			String pmString=fileString.substring(startIndex, finalIndex);
			String[] pmData=pmString.split(",");
			responseObject.put("pm", pm);
			responseObject.put("email", pmData[2]);
		}
		return responseObject.toString();
	}


	//DO NOT EXPOSE TO REST
	public static JSONObject getUsersUnexposed() throws IOException{
		JSONObject responseObject=new JSONObject();
		JSONArray responseArray=new JSONArray();
		String fileString=new String(Files.readAllBytes(Paths.get("config.txt")), StandardCharsets.UTF_8);
		//convert to JSONObject for client to read
		String[] pmArray=fileString.split(";");
		for (String pmUser:pmArray){
			String[] pmData=pmUser.split(",");
			if (pmData.length>1){
				String pm=pmData[0];
				String userString=new String(Files.readAllBytes(Paths.get(pm+".txt")), StandardCharsets.UTF_8);
				String[] userArray=userString.split(";");
				for (String user:userArray){
					String[] userData=user.split(",");
					if (userData.length>1){
						JSONObject tempObject=new JSONObject();
						tempObject.put("username", userData[0]);
						tempObject.put("password", userData[1]);
						tempObject.put("url", userData[2]);
						tempObject.put("alias", userData[3]);
						tempObject.put("seaMin", userData[4]);
						tempObject.put("seaMax", userData[5]);
						tempObject.put("eeaMin", userData[6]);
						tempObject.put("eeaMax", userData[7]);
						tempObject.put("bugMax", userData[8]);
						tempObject.put("email", pmData[2]);
						responseArray.put(tempObject);
					}
				}
			}
		}
		responseObject.put("users", responseArray);
		return responseObject;
	}
}
