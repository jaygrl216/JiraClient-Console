//setup credentials from cookie
var username=getCookie("username");
var password=getCookie("password");
var baseURL=getCookie("url");
var projectKey=getKeyFromURL();
var hostURL=window.location.host;
var metricResource="http://"+hostURL+"/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var allResource="http://" + hostURL+ "/pmportal/rest/metrics/all/" + username + "/" + password + "/" + baseURL;
var responseObject;
var id="sea";
var loaded=false;
var lineChart;
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
	alert("Failed to load metrics for this project. This could be either a problem with the server, the Jira instance, the project setup, or the credentials used. Reloading the page may fix the problem.")
}).done(function(jsonObject){
	responseObject=jsonObject;
	loaded=true;
	generateTable();
	drawLineGraphics();
});

//loading icon
$(document).ajaxStart(function(){
	$("#loadImage").show();

});
$(document).ajaxStop(function(){
	$("#loadImage").hide();
	$("#xlink").attr("href","data/output.xml");
	$("#elink").attr("href","data/output.xls");
});
//functions
function getKeyFromURL(){
	var temp = location.search.substring(1).split("=");
	if (temp.length>0){
		return temp[1];
	};
};
function selectResource(whichId){
	id=whichId;
	if (loaded){
	lineChart.destroy();
	drawLineGraphics();
	};
};
function toggleTable(){
	$("#dataTable").toggle();
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
function drawLineGraphics(){
	//for most metrics
	var chartLabel;
	var dataArray;
	var labelArray=[];
	var colorArray=[];
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
		colorArray[i]="#FF0000";
	}
	labelArray[dataArray.length-1]="Next Sprint";
	colorArray[dataArray.length-1]="#0000ff";
	var chartData = {
			labels:labelArray,
			datasets: [
			           {
			        	   label:chartLabel,
			        	   data:dataArray,
			        	   fill:false,
						   pointRadius:5,
						   pointHoverRadius:6,
						   backgroundcolor:"#FF0000",
			        	   pointBackgroundColor:colorArray,
			        	   borderColor:'rgba(255, 255, 255, 0.5)',
			        	   borderDash:[5,5],
			        	   lineTension:0
			           }]

	};
	lineChart = new Chart(ctx,{
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
	var seaAnalysis="The next sprint's predicted SEA value is<br>" + predictedSea + ".";
	var seaAnalysis2="This means the next sprint may take " + Math.round(predictedSea *10)/10 + " times as long as<br>estimated.";
	var seaAnalysis3="";
	if (predictedSea > 1){
		seaAnalysis3="Your SEA is higher than 1, which means that sprints are<br>taking longer than they should. Estimating longer periods, <br>lowering the amount of work per sprint, or making sure<br>each team member is performing as they should may help<br>reduce this score."
	}else if (predictedSea < 1){
				seaAnalysis3="Your SEA is lower than 1, which means that sprints are<br>taking less time than they should. Estimating shorter periods or <br>increasing the amount of work per sprint may help <br>increase this score."
	}else{
		seaAnalysis3="Your SEA is exactly 1, which means that the time spent on<br> sprints is exactly as estimated, down to the millisecond."
	};
	var predictedEea=eeaArray[eeaArray.length-1];
	var eeaAnalysis="Your predicted EEA value for the next sprint is<br>" + predictedEea + ".";
	var eeaAnalysis2="";
	if (predictedEea==1){
		eeaAnalysis2="This means effort estimation planning is going as it should.";
	}else{
		eeaAnalysis2="The next sprint may take " + Math.round(predictedEea *10)/10 + " times as much effort as<br>estimated.";
	}
	var predictedBug=bugArray[bugArray.length-1];
	var bugAnalysis="Your predicted Bug count for the next sprint is " + predictedBug + ".";
	$("#reportContainer").html("<h4>SEA</h4><p>" +seaAnalysis+"</p>"+"<p>" +seaAnalysis2+"</p>"+"<p>" +seaAnalysis3+"</p>"+"<hr>EEA</h4><p>" +eeaAnalysis+"</p>"+"<p>" +eeaAnalysis2+"</p>"+"<h4>Bugs</h4><p>" +bugAnalysis+"</p>");
};
function generateTable(){
	seaArray=JSON.parse(responseObject.sea);
	eeaArray=JSON.parse(responseObject.eea);
	bugArray=JSON.parse(responseObject.bugs);
//subtract 1 to neglect prediction
	for (var i=0;i<seaArray.length-1; i++){
		$("#dataTable").append("<tr><td> Sprint: "+ (i+1)+ "</td><td>"+seaArray[i]+"</td><td>"+eeaArray[i]+"</td><td>"+bugArray[i]+"</td></tr>");
	};
};
function redirectToAll(){
	window.location="metric_all.html";
};