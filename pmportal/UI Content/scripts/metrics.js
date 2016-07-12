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
	//graphics
$(document).ajaxStop(function(){
	var seaArray=JSON.parse(responseObject.sea);
	var labelArray;
	for (var i=0; i<seaArray.length(); i++){
		labelArray[i]="Sprint " + i;
	}
	var ctx = document.getElementById("chart");
	var chartData = {
			labels:labelArray,
				datasets: [
	        {
	        	label:"SEA",
	            data:seaArray,
	            fill:false,
	            backgroundColor:"#FF6384"
	        }]
	};
	var myLineChart = new Chart(ctx,{
	    type: 'line',
	    data: chartData,
	});
});