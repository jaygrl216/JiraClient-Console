var username="amital";
var password="ComPuteR90";
var projectKey="PMPOR";
var baseURL="http://54.152.100.242/jira";
var hostURL=window.location.host;
var metricResource="http://"+hostURL+"/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var projectResource="http://"+hostURL+"/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var responseObject;
var id="sea";
var loaded=false;
	var ctx = document.getElementById("chart").getContext("2d");
ctx.canvas.originalwidth = ctx.canvas.width;
ctx.canvas.originalheight = ctx.canvas.height;

//retrieve data
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


//styling for window resize
$( window ).resize(function() {
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

function drawGraph(){

	if (id=="getReport"){
		generateReport();
	}else if (id=="dataList"){
		generateTable();
	}else{
		drawLineGraphics();
	};
	$(".chartjs-hidden-iframe").remove();
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
ctx.canvas.width = ctx.canvas.originalwidth;
ctx.canvas.height = ctx.canvas.originalheight;
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

function generateReport(){
	var seaArray=JSON.parse(responseObject.sea);
	var eeaArray=JSON.parse(responseObject.eea);
	var bugArray=JSON.parse(responseObject.bugs);
	var predictedSea=seaArray[seaArray.length-1];
	var seaAnalysis="Your predicted SEA value for the next sprint is " + predictedSea + ".";
	var seaAnalysis2="This means that the next sprint is estimated to take roughly " + predictedSea.round + " times as long to finish than estimated.";
	var predictedEea=eeaArray[eeaArray.length-1];
	var eeaAnalysis="Your predicted EEA value for the next sprint is " + predictedEea + ".";
	var eeaAnalysis2="This means that the next sprint is estimated to take roughly " + predictedEea.round + " times as much effort to finish than estimated.";
	var predictedBug=beaArray[beaArray.length-1];
	var bugAnalysis="Your predicted Bug count for the next sprint is " + predictedBug + ".";
	
	
};
function generateTable(){
	
}