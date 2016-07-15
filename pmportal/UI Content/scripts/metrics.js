var username="amital";
var password="ComPuteR90";
var projectKey=getProject();
var baseURL="http://54.152.100.242/jira";
var hostURL=window.location.host;
var metricResource="http://"+hostURL+"/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var projectResource="http://"+hostURL+"/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var responseObject;
var id="sea";
var loaded=false;
var ctx = document.getElementById("chart").getContext("2d");
$("h3").append(projectKey);
$("#aboutInfo").hide();
//retrieve data
$.ajax({
	type:"GET",
	dataType:"json",
	url:metricResource
}).fail(function( xhr, status, errorThrown ) {
	console.log( "Error: " + errorThrown );
	console.log( "Status: " + status );
	console.dir( xhr );
	alert("Failed to load metrics for this project. This could be either a problem with the server, the Jira instance, or the project setup. Reloading the page may solve the problem.")
}).done(function(jsonObject){
	responseObject=jsonObject;
	loaded=true;
	generateTable();
	drawGraph();
});

//loading icon
$(document).ajaxStart(function(){
	$("#loadImage").show();
});
$(document).ajaxStop(function(){
	$("#loadImage").hide();
});

//functions
 function getProject(){
    var temp = location.search.substring(1).split("=");
    var key=temp[1];
	if (key!=null){
	return key;
	}else{
		return "DEV";
	};
  };

function selectResource(whichId){
	id=whichId;
	if (loaded){
		drawGraph();
	};
};


function toggleTable(){
	if(	$("#dataTable").css("visibility")=="collapse"){
		$("#dataTable").css("visibility", "visible");
	}else{
		$("#dataTable").css("visibility", "collapse");
	};
};

function showReport(){
	if (loaded){
		generateReport();
	} else{
		$("#report").html("<p>Please try again after data is loaded.<p>");
	}; 
};
function toggleAbout(){
	$("#aboutInfo").toggle();
};


function drawGraph(){
	$(".chartjs-hidden-iframe").remove();
	drawLineGraphics();
};

function drawLineGraphics(){
	//for most metrics
	var chartLabel;
	var dataArray;
	var labelArray=[];
	if (id=="sea"){
		chartLabel="SEA";
		dataArray=JSON.parse(responseObject.sea);
	}else if(id=="eea"){
		chartLabel="EEA";
		dataArray=JSON.parse(responseObject.eea);
	}else if (id=="bugs"){
		chartLabel="Bugs";
		dataArray=JSON.parse(responseObject.bugs);
	};

	for (var i=0; i<dataArray.length; i++){
		labelArray[i]="Sprint " + (i+1);
	}
	labelArray[dataArray.length-1]="Next Sprint";
	var chartData = {
			labels:labelArray,
			datasets: [
			           {
			        	   label:chartLabel,
			        	   data:dataArray,
			        	   fill:false,
			        	   backgroundColor:"#FF0000",
						   borderColor:'rgba(0, 0, 0, 0.25)',
						   lineTension:0
			           }]

	};
	var myLineChart = new Chart(ctx,{
		type: 'line',
		data: chartData,
		options:{
			maintainAspectRatio:true,
			responsive:true
		}
	});
};

function generateReport(){
	var seaArray=JSON.parse(responseObject.sea);
	var eeaArray=JSON.parse(responseObject.eea);
	var bugArray=JSON.parse(responseObject.bugs);
	var predictedSea=seaArray[seaArray.length-1];
	var seaAnalysis="The next sprint's predicted SEA value is " + predictedSea + ".";
	var seaAnalysis2="This means the next sprint may take " + Math.round(predictedSea *10)/10 + " times <br>as long as estimated.";
	var predictedEea=eeaArray[eeaArray.length-1];
	var eeaAnalysis="Your predicted EEA value for the next sprint is " + predictedEea + ".";
	var eeaAnalysis2="";
	if (predictedEea==1){
		eeaAnalysis2="This means effort estimation planning is going as it should.";
	}else{
		eeaAnalysis2="The next sprint may take " + Math.round(predictedEea *10)/10 + " times <br>as much effort than estimated.";
	}
	var predictedBug=bugArray[bugArray.length-1];
	var bugAnalysis="Your predicted Bug count for the next sprint is " + predictedBug + ".";
	$("#reportContainer").html("<p>" +seaAnalysis+"</p>"+"<p>" +seaAnalysis2+"</p>"+"<p>" +eeaAnalysis+"</p>"+"<p>" +eeaAnalysis2+"</p>"+"<p>" +bugAnalysis+"</p>");
	$("#reportContainer").css("background-color", "#FFFFFF");
	$("#reportContainer").css("color", "#000000");
};
function generateTable(){
	seaArray=JSON.parse(responseObject.sea);
	eeaArray=JSON.parse(responseObject.eea);
	bugArray=JSON.parse(responseObject.bugs);
//	arrays have same length, subtract 1 to neglect prediction
	for (var i=0;i<seaArray.length-1; i++){
		$("#dataTable").append("<tr><td> Sprint: "+ (i+1)+ "</td><td>"+seaArray[i]+"</td><td>"+eeaArray[i]+"</td><td>"+bugArray[i]+"</td></tr>");
	};
};