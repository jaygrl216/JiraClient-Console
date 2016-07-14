var username="amital";
var password="ComPuteR90";
var projectKey="DEV";
var baseURL="http://54.152.100.242/jira";
var hostURL=window.location.host;
var metricResource="http://"+hostURL+"/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var projectResource="http://"+hostURL+"/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var responseObject;
var id="sea";
var loaded=false;
var ctx = document.getElementById("chart").getContext("2d");
$("h3").append(projectKey);
//retrieve data
$.ajax({
	type:"GET",
	dataType:"json",
	url:metricResource
}).fail(function( xhr, status, errorThrown ) {
	console.log( "Error: " + errorThrown );
	console.log( "Status: " + status );
	console.dir( xhr );
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

function selectResource(whichId){
	id=whichId;
	if (loaded){
		drawGraph();
	};
};


function toggleTable(){
	if(	$("#dataTable").css("visibility")=="collapse"){
		$("#dataTable").css("visibility", "visible");
		$("#report").css("display", "none");
	}else{
		$("#dataTable").css("visibility", "collapse");
	};
};

function drawGraph(){

	if (id=="getReport"){
		generateReport();
	}else if (id=="dataList"){
		generateTable();
	}else{
		$(".chartjs-hidden-iframe").remove();
		drawLineGraphics();
	};
}

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
			        	   backgroundColor:"#FF0000"
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
	var seaAnalysis="Your predicted SEA value for the next sprint is " + predictedSea + ".";
	var seaAnalysis2="This means that the next sprint is estimated to take roughly " + Math.round(predictedSea *10)/10 + " times as long to finish than estimated.";
	var predictedEea=eeaArray[eeaArray.length-1];
	var eeaAnalysis="Your predicted EEA value for the next sprint is " + predictedEea + ".";
	var eeaAnalysis2="";
	if (predictedEea==1){
		eeaAnalysis2="This means that effort estimation planning is going as it should.";
	}else{
		eeaAnalysis2="This means that the next sprint is estimated to take roughly " + Math.round(predictedEea *10)/10 + " times as much effort to finish than estimated.";
	}
	var predictedBug=bugArray[bugArray.length-1];
	var bugAnalysis="Your predicted Bug count for the next sprint is " + predictedBug + ".";
	$("#report").html("<p>" +seaAnalysis+"</p>"+"<p>" +seaAnalysis2+"</p>"+"<p>" +eeaAnalysis+"</p>"+"<p>" +eeaAnalysis2+"</p>"+"<p>" +bugAnalysis+"</p>");
	$("#dataTable").css("visibility", "collapse");
	$("#report").css("display", "initial");
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