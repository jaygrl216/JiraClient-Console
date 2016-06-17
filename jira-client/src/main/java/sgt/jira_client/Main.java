package sgt.jira_client;

import java.util.Scanner;

/**
 * Entry-point invoked when the jar is executed.
 */
public class Main {

    private static String jiraURL;
    private static String username;
    private static String password;
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
    	 StringBuilder intro = new StringBuilder();
         intro.append("*************************************************************\r\n");
         intro.append("*********** Welcome to the Jira JAVA REST Client ************\r\n");
         intro.append("*************************************************************\r\n");
         System.out.println(intro.toString());
         
         System.out.println("This program will be used to access your Jira.");
         System.out.println("Where is your Jira located?");
         jiraURL = input.nextLine();
         
         
         
    }
      
}