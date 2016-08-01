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
var progData=[];
$("#aboutInfo").hide();
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
		labelArray[index]=proj.name;
	});
	getMetrics();
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
function getMetrics(){
	$.each(projectNameArray, function (index, proj) {;
	var resource="http://"+hostURL+"/pmportal/rest/metrics/sea/"+proj.key+"/" +username + "/" + password + "/" + baseURL;
		seaData[index]=getData(resource).sea;
	});
	drawSEA();
	$.each(projectNameArray, function (index, proj) {;
	var resource="http://"+hostURL+"/pmportal/rest/metrics/eea/"+proj.key+ "/" +username + "/" + password + "/" + baseURL;
		eeaData[index]=getData(resource).eea;
	});
	drawEEA();
	$.each(projectNameArray, function (index, proj) {;
	var resource="http://"+hostURL+"/pmportal/rest/metrics/bugs/"+proj.key+ "/"+username + "/" + password + "/" + baseURL;
		bugData[index]=getData(resource).bugs;
	});
	drawBugs();
	$.each(projectNameArray, function (index, proj) {;
	var resource="http://"+hostURL+"/pmportal/rest/metrics/progress/"+proj.key+ username + "/" + password + "/" + baseURL;
		progData[index]=getData(resource).progress;
	});
	drawProgress();
};
function getData(resource){
	$.ajax({
	url: resource,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
	metricObject = jsonObject;
	return metricObject;
});
};
function drawSEA(){
		var ctx1=document.getElementById("seaGraph").getContext("2d");
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
		var barChart = new Chart(ctx1,{
		type: 'bar',
		data: chartData1,
		options:{
			maintainAspectRatio:false,
			responsive:true,
			scales: {
				yAxes: [{
					ticks: {
						beginAtZero:true
					}
				}]
			}
		}
	});
};
function drawEEA(){
	var ctx2=document.getElementById("eeaGraph").getContext("2d");
			var chartData2 = {
			labels:labelArray,
			datasets: [
			           {
			        	   label:"EEA",
			        	   data:eeaData,
			        	   fill:false,
			        	   backgroundColor:"#00FF00",
			           }]

	};
	var barChart2 = new Chart(ctx2,{
		type: 'bar',
		data: chartData2,
		options:{
			maintainAspectRatio:false,
			responsive:true,
			scales: {
				yAxes: [{
					ticks: {
						beginAtZero:true
					}
				}]
			}
		}
	});
};
function drawBugs(){
	var ctx3=document.getElementById("bugGraph").getContext("2d");
			var chartData3 = {
			labels:labelArray,
			datasets: [
			           {
			        	   label:"Bugs",
			        	   data:bugData,
			        	   fill:false,
			        	   backgroundColor:"#0000FF",
			           }]

	};
	var barChart3 = new Chart(ctx3,{
		type: 'bar',
		data: chartData3,
		options:{
			maintainAspectRatio:false,
			responsive:true,
			scales: {
				yAxes: [{
					ticks: {
						beginAtZero:true,
						fixedStepSize:1
					}
				}]
			}
		}
	});
	
};

function drawProgress(){
	var ctx4=document.getElementById("progGraph").getContext("2d");
	var chartData4 = {
		labels:labelArray,
		datasets: [
		           {
		        	   label:"Progress",
		        	   data:progData,
		        	   fill:false,
		        	   backgroundColor:"#FF00FF",
		           }]

	};
	var barChart4 = new Chart(ctx4,{
		type: 'bar',
		data: chartData4,
		options:{
			maintainAspectRatio:false,
			responsive:true,
			scales: {
				yAxes: [{
					ticks: {
						beginAtZero:true,
						max:100
					}
				}]
			}
		}
	});
};
function toggleAbout(){
	$("#aboutInfo").toggle();
};
function redirectToMetrics(key){
	window.location="metrics.html?project="+key;
};