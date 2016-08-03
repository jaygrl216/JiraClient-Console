package com.sgt.pmportal.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScheduleService {

	public void checkMetrics(){
		

	}
	public JSONObject getCredentials() throws IOException{
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
		return responseObject;
	}

}
