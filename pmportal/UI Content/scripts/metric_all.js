var username=getCookie("username");
var password=getCookie("password");
var baseURL=getCookie("url");
var hostURL=window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var allResource="http://" + hostURL+ "/pmportal/rest/metrics/all/" + username + "/" + password + "/" + baseURL;
var responseObject;
var metricObject;
var projectNameArray;
var projectArray;
var labelArray=[];
var seaData=[];
var eeaData=[];
var bugData=[];
$.ajax({
	url: homeResource,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
	responseObject = jsonObject;
	projectNameArray = responseObject.projects;
	$.each(projectNameArray, function (index, proj) {;
		$("#projectList").append("<li>" + proj.name +  "</li>");
	});
});
$.ajax({
	url: allResource,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
	metricObject = jsonObject;
	projectArray = metricObject.project;
	$.each(projectArray, function (index, proj) {;
		labelArray[index]=proj.name;
	});
	drawGraphics();
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
		$.each(projectNameArray, function (i, proj) {
			if(proj.name == item) {
				redirectToMetrics(proj.key);
			}
		});
	});
});
function parseData(){
	$.each(projectArray, function (i, proj) {;
		seaData[i]=JSON.parse(proj.sea);
		eeaData[i]=JSON.parse(proj.eea);
		bugData[i]=JSON.parse(proj.bugs);
	});
};


function drawGraphics(){
	var ctx1=document.getElementById("seaGraph").getContext("2d");
	var ctx2=document.getElementById("eeaGraph").getContext("2d");
	var ctx3=document.getElementById("bugGraph").getContext("2d");
	var chartData1 = {
			labels:labelArray,
			datasets: [
			           {
			        	   label:"SEA",
			        	   data:seaData,
			        	   fill:false,
			        	   backgroundColor:"#FF0000",
			           }]

	};
		var chartData2 = {
			labels:labelArray,
			datasets: [
			           {
			        	   label:"EEA",
			        	   data:eeaData,
			        	   fill:false,
			        	   backgroundColor:"#FF0000",
			           }]

	};
		var chartData3 = {
			labels:labelArray,
			datasets: [
			           {
			        	   label:"Bugs",
			        	   data:bugData,
			        	   fill:false,
			        	   backgroundColor:"#FF0000",
			           }]

	};
	var barChart = new Chart(ctx1,{
		type: 'bar',
		data: chartData1,
		options:{
			maintainAspectRatio:true,
			responsive:true
		}
	});
		var barChart2 = new Chart(ctx2,{
		type: 'bar',
		data: chartData2,
		options:{
			maintainAspectRatio:true,
			responsive:true
		}
	});
		var barChart3 = new Chart(ctx3,{
		type: 'bar',
		data: chartData3,
		options:{
			maintainAspectRatio:true,
			responsive:true
		}
	});
};


function redirectToMetrics(key){
	window.location="metrics.html?project="+key;
};