var username=getCookie("username");
if (username==""){
	window.location="index.html";
};
var password=getCookie("password");
var baseURL=getCookie("url");
var hostURL=window.location.host;
var projectResource = "http://"+hostURL+"/pmportal/rest/metrics/all/projects/" + username + "/" + password + "/" + baseURL;
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
	url: projectResource,
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
	parseData();
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
		seaData[i]=proj.sea;
		eeaData[i]=proj.eea;
		bugData[i]=proj.bugs;
		progData[i]=proj.progress;
	});
};


function drawGraphics(){
	var ctx1=document.getElementById("seaGraph").getContext("2d");
	var ctx2=document.getElementById("eeaGraph").getContext("2d");
	var ctx3=document.getElementById("bugGraph").getContext("2d");
	var ctx4=document.getElementById("progGraph").getContext("2d");
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
			        	   backgroundColor:"#00FF00",
			           }]

	};
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