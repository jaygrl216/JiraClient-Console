package sgt.jira_client;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;


import java.net.URI;
import java.util.Base64;


public class Test {

    private static final String JIRA_URL = "http://52.204.130.7:8081";
    private static final String JIRA_ADMIN_USERNAME = "JWashington@sgt-inc.com";
    private static final String JIRA_ADMIN_PASSWORD = "Diamond2017";
    
    private static String encodeAuth(String username, String pass) {
    	return Base64.getUrlEncoder().encodeToString((username + ":" + pass).getBytes());
    }

    public static void main(String[] args) throws Exception {
    	
        // Print usage instructions
        StringBuilder intro = new StringBuilder();
        intro.append("*************************************************************\r\n");
        intro.append("************ JIRA Java REST Client ('JRJC') *****************\r\n");
        intro.append("*************************************************************\r\n");
        System.out.println(intro.toString());

        // Construct the JRJC client
        System.out.println(String.format("Logging in to %s with authentication '%s'", JIRA_URL, 
        		encodeAuth(JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD)));
        
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI uri = new URI(JIRA_URL);
        JiraRestClient client = factory.createWithBasicHttpAuthentication
        		(uri, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);

        // Invoke the JRJC Client
        Promise<User> promise = client.getUserClient().getUser("JWashington@sgt-inc.com");
        Promise<Issue> promise2 = client.getIssueClient().getIssue("PM-9");
        User user = promise.claim();
        Issue issue = promise2.claim();

        // Print the result
        System.out.println(String.format("Your admin user's name is: %s", 
        		user.getDisplayName()));
        System.out.println(String.format("Your admin user's email address is: %s\r\n", 
        		user.getEmailAddress()));
        System.out.println(String.format("Your admin user's timezone is: %s\r\n", 
        		user.getTimezone()));
        System.out.println(String.format("The issue being worked on is %s", 
        		issue.getDescription()));

        // Done
        System.out.println("Example complete. Now exiting.");
        System.exit(0);
    }
}