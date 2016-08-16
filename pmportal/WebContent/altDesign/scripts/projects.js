var hostURL=window.location.host;
var username=getCookie("username");
var password=getCookie("password");
var url=getCookie("url");




//resource allocation
var userResource="http://" + hostURL + "/pmportal/rest/user/"+username+"/"+password+"/"+url;
var userArray;
var userLabelArray[];
var effortArray[];
$.ajax({
	url: userResource,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
	userArray = responseObject.users;
	$.each(userArray, function (index, user) {;
		userLabelArray[index]=user.name + ", " + user.numIssues + " issues";
		effortArray[index]=user.effort;
	});
	graphEffort();
});
function graphEffort(){
var ctx=document.getElementById("resourceAllocation").getContext("2d");
	var effortData = {
			labels:userLabelArray,
			datasets: [
			           {
			        	   label:"Story Points",
			        	   data:effortArray,
			        	   fill:false,
			        	   backgroundColor:"#FF0000",
			           }]

	};
var resoureChart = new Chart(ctx,{
		type: 'bar',
		data: effortData,
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
}