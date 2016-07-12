var username = "jwashington"
var password = "Diamond2017"
//var projectKey="PMPOR";
var baseURL = "http://54.152.100.242/jira";
var homeResource = "http://localhost:8080/pmportal/rest/home/" + username + "/" + password + "/" +baseURL;
var responseObject;

$.ajax({
url:homeResource,
dataType:"json"
}).fail(function( xhr, status, errorThrown ) {
    console.log( "Error: " + errorThrown );
    console.log( "Status: " + status );
    console.dir( xhr );
}).done(function(jsonObject){
	responseObject = jsonObject;
});

// creating list of elements
$(document).ajaxStop(function(){
	var projectArray = JSON.parse(responseObject.project);
	var labelArray;
	for (var i=0; i<seaArray.length(); i++){
		labelArray[i]="Sprint " + i;
	}
});