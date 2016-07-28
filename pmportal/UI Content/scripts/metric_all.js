var username=getCookie("username");
var password=getCookie("password");
var baseURL=getCookie("url");
var hostURL=window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var allResource="http://" + hostURL+ "/pmportal/rest/metrics/all/" + username + "/" + password + "/" + baseURL;
var responseObject;
var projectArray;
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
	$.each(projectArray, function (index, proj) {
		var num = index + 1;
		$("#projectList").append("<li>" + proj.name +  "</li>");
	});
});
$(document).ajaxStart(function(){
	$("#loadImage").show();
});
$(document).ajaxStop(function(){
	$("#loadImage").hide();
});
$(document).ready(function(){
	$("#projectList").on( "click", "li" , function () {
		var item = $(this).text();
		$.each(projectArray, function (index, proj) {
			if(proj.name == item) {
				redirectToMetrics(proj.key);
			}
		});
	});
});

function redirectToMetrics(key){
	window.location="metrics.html?project="+key;
};