var username="amital";
var password="ComPuteR90";
var projectKey="PMPOR";
var baseURL="http://54.152.100.242/jira";
var metricResource="http://localhost:8080/pmportal/rest/metrics/project/detail/"+projectKey+"/" + username + "/" + password + "/" +baseURL;
var responseObject;

$.ajax({
url:metricResource,
dataType:"json"
}).fail(function( xhr, status, errorThrown ) {
    console.log( "Error: " + errorThrown );
    console.log( "Status: " + status );
    console.dir( xhr );
}).done(function(jsonObject){
	responseObject=jsonObject;
});
	//after ajax stops, or else responseObject is undefined (asynchronicity)
$(document).ajaxStop(function(){
	//if it's not parsed, the chart won't see it as an array
	var dataArray=JSON.parse(responseObject.sea);
	var labelArray;
	for (var i=0; i<dataArray.length(); i++){
		labelArray[i]="Sprint " + i;
	}
	var ctx = document.getElementById("chart");
	var chartData = {
			labels:labelArray,
				datasets: [
	        {
	        	label:"SEA",
	            data:dataArray,
	            fill:false,
	            backgroundColor:"#FF6384"
	        }]
	};
	var myLineChart = new Chart(ctx,{
	    type: 'line',
	    data: chartData,
	});
});