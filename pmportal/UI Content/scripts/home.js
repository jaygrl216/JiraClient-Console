var username=getCookie("username").toString();
var password=getCookie("password").toString();
var baseURL=getCookie("url").toString();
var hostURL = window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var issueResource = "http://"+hostURL+"/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;
var metricResource = "http://"+hostURL+"/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;
var responseObject;
var responseObject2;
var responseObject3;
var projectArray;
var issueArray;
var metrics;
var projKey;
var stop = 0;
var completed = 0;

$.ajax({
	url: homeResource,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
	responseObject = jsonObject;

	projectArray = responseObject.projects;
    
//    $.each(projectArray, function (index, proj) {
//			getProgress(proj);
//		});
});

$(document).ajaxStop(function () {
        $("#total").append("<p class='totalProjects'>" + projectArray.length + "</p>");
//         $("#finished").append("<p class='totalProjects'>" + completed + "</p>");
});

function getProgress(project) {
    projKey = project.key;
    metricResource = "http://"+hostURL+"/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;
    $.ajax({
        url: metricResource,
        dataType: "json"
    }).fail(function(xhr, status, errorThrown ) {
        console.log("Error: " + errorThrown );
        console.log("Status: " + status );
        console.dir(xhr);
    }).done(function(jsonObject){
        metrics = jsonObject;
	   if(metrics.progress == 100) {
           completed = completed + 1;
       }
    });
}

