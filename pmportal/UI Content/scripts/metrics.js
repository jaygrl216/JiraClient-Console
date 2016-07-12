var username="amital";
var password="ComPuteR90";
var projectKey="PMPOR";
var baseURL="http://54.152.100.242/jira";
var metricResource="http://localhost:8080/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var projectResource="http://localhost:8080/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var responseObject;
var projectObject;
var id="sea";
var loaded=false;

$.ajax({
	url:metricResource,
	dataType:"json"
}).fail(function( xhr, status, errorThrown ) {
	console.log( "Error: " + errorThrown );
	console.log( "Status: " + status );
	console.dir( xhr );
}).done(function(jsonObject){
	responseObject=jsonObject;
	loaded=true;
	drawGraph();

});
//functions
function selectResource(whichId){
	id=whichId;
	if (loaded){
		drawGraph();
	};
};

function drawGraph(){
	if (id=="projects"){
		//for defects by project
		drawProjectGraphics();
	}else{
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
	var ctx = document.getElementById("chart");
	var chartData = {
			labels:labelArray,
			datasets: [
			           {
			        	   label:chartLabel,
			        	   data:dataArray,
			        	   fill:false,
			        	   backgroundColor:"#FF6384"
			           }]
	};
	var myLineChart = new Chart(ctx,{
		type: 'line',
		data: chartData,
	});
};

function drawProjectGraphics(){
	$.ajax({
		url:projectResource,
		dataType:"json"
	}).fail(function( xhr, status, errorThrown ) {
		console.log( "Error: " + errorThrown );
		console.log( "Status: " + status );
		console.dir( xhr );
	}).done(function(jsonObject){
		projectObject=jsonObject;
	});
};