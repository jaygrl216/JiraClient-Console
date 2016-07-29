var username=getCookie("username").toString();
var password=getCookie("password").toString();
var baseURL=getCookie("url").toString();
var hostURL = window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var issueResource = "http://"+hostURL+"/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;
var metricResource = "http://"+hostURL+"/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;
var responseObject;
var responseObject2;
var responseObject3;
var projectArray;
var issueArray;
var metrics;
var barChart;
var pieChart;
var projKey;
var stop = 0;

var barData = {
		labels: [],
		datasets: [
		           {
		        	   label: 'Issues Created',
		        	   backgroundColor: "rgba(105, 214, 209, 0.3)",
		        	   borderColor: "rgba(105, 214, 209, 0.8)",
		        	   data: []
		           },
		           {
		        	   label: 'Issues Completed',
		        	   backgroundColor: "rgba(67, 242, 61, 0.3)",
		        	   borderColor: "rgba(67, 242, 61, 0.8)",
		        	   data: []
		           }
		           ]
};


var pieData = {
		labels: [
		         "Completed",
		         "To Do",
		         ],

		         datasets: [
		                    {
		                    	data: [0,0],
		                    	backgroundColor: [
		                    	                  "#FA760A",
		                    	                  "#FAEB48",
		                    	                  ],
		                    	                  hoverBackgroundColor: [
		                    	                                         "#FA9848",
		                    	                                         "#FAF087",
		                    	                                         ]
		                    }]
};

function createBar() {
	var ctx = document.getElementById('issues').getContext('2d');
	barChart = new Chart(ctx, {
		type: 'bar',
		data: barData,
		options: {
			maintainAspectRatio: false,
			responsive: true,
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

function createPie() {
	var ctx = document.getElementById('pie').getContext('2d');
	pieChart = new Chart(ctx, {
		type: 'pie',
		data: pieData,
		options: {
			maintainAspectRatio: true,
			responsive: true
		}
	});
}

$.ajax({
	url: homeResource,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
	responseObject = jsonObject;

	projectArray = responseObject.projects;

	$.each(projectArray, function (index, proj) {
		var num = index + 1;
		$("#projectList").append("<li>" + proj.name +  "</li>");
	});
});


$(document).ajaxStop(function () {
    if(stop == 0) {
        createBar();
        createPie();
        showInitialData();
        stop = 1;
    }
});

$(document).ready(function(){
	$("#topLayer").on( "click", "li" , function () {
		var item = $(this).text();
		$.each(projectArray, function (index, proj) {
			if(proj.name == item) {
				showProjectData(index);
			}
		});
	});
    
    $('#calendar').fullCalendar({
        eventColor: '#378006',
        contentHeight: 'auto',
        backgroundColor: '#10799C',
        borderColor: '#10799C'
    })
});


function createBar() {
	var ctx = document.getElementById('issues').getContext('2d');
	barChart = new Chart(ctx, {
		type: 'bar',
		data: barData,
		options: {
			maintainAspectRatio: false,
			responsive: true,
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

function createPie() {
	var ctx = document.getElementById('pie').getContext('2d');
	pieChart = new Chart(ctx, {
		type: 'pie',
		data: pieData,
		options: {
			maintainAspectRatio: true,
			responsive: true
		}
	});
}



function showInitialData() {
	var project = projectArray[0];
    projKey = project.key;
	//this will pass the information to metrics, so when they click on the link, it continues with the same project
	$("#metricLink").attr("href", "metrics.html?project=" + projKey);
    
    metricResource = "http://"+hostURL+"/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;
    issueResource = "http://"+hostURL+"/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;

	$.ajax({
		url: metricResource,
		dataType: "json"
	}).fail(function(xhr, status, errorThrown ) {
		console.log("Error: " + errorThrown );
		console.log("Status: " + status );
		console.dir(xhr);
	}).done(function(jsonObject){
		responseObject3 = jsonObject;
		metrics = responseObject3;
		pieData.datasets[0].data[0] = Math.round(metrics.progress * 100) / 100;
		pieData.datasets[0].data[1] = Math.round((100 - metrics.progress) * 100) / 100;
		createPie();
        
        var seaData = Math.round(metrics.sea * 100) / 100;
        var eeaData = Math.round(metrics.eea * 100) / 100;
        var progressData = Math.round(metrics.progress * 100) / 100;
        
        $("#graph").empty();
        $("#graph").append("<h4> Project Data & Info </h4>").append
        ("<p> Project Name: "+ project.name + " \(" + project.key + "\)</p>");
        
         if(seaData < 1) {
            $("#graph").append("<p class='good'> SEA: " + seaData + "</p>");
        } else if (seaData >= 1) {
            $("#graph").append("<p class='bad'> SEA: " + seaData + "</p>");
        } else {
            $("#graph").append("<p class='warning'> SEA: Cannot Compute SEA </p>");
        }
        
        if(eeaData < 1) {
            $("#graph").append("<p class='good'> EEA: " + eeaData + "</p>");
        } else if (eeaData >= 1) {
            $("#graph").append("<p class='bad'> EEA: " + eeaData + "</p>");
        } else {
            $("#graph").append("<p class='warning'> EEA: Cannot Compute SEA </p>");
        }
        
        $("#graph").append("<p> Progress: " + progressData + "</p>");
	});

	$.ajax({
		url: issueResource,
		dataType: "json"
	}).fail(function(xhr, status, errorThrown ) {
		console.log("Error: " + errorThrown );
		console.log("Status: " + status );
		console.dir(xhr);
	}).done(function(jsonObject){
		console.log("SUCCESS");
		responseObject2 = jsonObject;
		issueArray = responseObject2.issues;
		var completed = 0;
		var toDo = 0;

		$.each(issueArray, function (index, issue) {
			if(issue.status == "Resolved" || issue.status == "Closed" || issue.status == "Done") {
				completed = completed + 1;
			} else {
				toDo = toDo + 1;
			}
		});
		barData.datasets[0].data[0] = issueArray.length
		barData.datasets[1].data[0]= completed;
		createBar();
	});
}

function showProjectData(num) {
    var project = projectArray[num];
    projKey = project.key;
    issueResource = "http://localhost:8080/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;
    metricResource = "http://localhost:8080/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;
    
    $.ajax({
        url: metricResource,
        dataType: "json"
    }).fail(function(xhr, status, errorThrown ) {
        console.log("Error: " + errorThrown );
        console.log("Status: " + status );
        console.dir(xhr);
    }).done(function(jsonObject){
        responseObject3 = jsonObject;
        metrics = responseObject3;
        pieData.datasets[0].data[0] = Math.round(metrics.progress * 100) / 100;
        pieData.datasets[0].data[1] = Math.round((100 - metrics.progress) * 100) / 100;
        pieChart.update();
        var seaData = Math.round(metrics.sea * 100) / 100;
        var eeaData = Math.round(metrics.eea * 100) / 100;
        var progressData = Math.round(metrics.progress * 100) / 100; 
        
         $("#graph").empty();
        $("#graph").append("<h4> Project Data & Info </h4>").append
        ("<p> Project Name: "+ project.name + " \(" + project.key + "\)</p>");
        
        if(seaData < 1) {
            $("#graph").append("<p class='bad'> SEA: " + seaData + "</p>");
        } else if (seaData >= 1) {
            $("#graph").append("<p class='good'> SEA: " + seaData + "</p>");
        } else {
            $("#graph").append("<p class='warning'> SEA: Cannot Compute SEA </p>");
        }
        
        if(eeaData < 1) {
            $("#graph").append("<p class='bad'> EEA: " + eeaData + "</p>");
        } else if (eeaData >= 1) {
            $("#graph").append("<p class='good'> EEA: " + eeaData + "</p>");
        } else {
            $("#graph").append("<p class='warning'> EEA: Cannot Compute SEA </p>");
        }
        
        $("#graph").append("<p> Progress: " + progressData + "</p>");
    });
        
        
    //this will pass the information to metrics, so when they click on the link, it continues with the same project
	$("#metricLink").attr("href", "metrics.html?project=" + projKey);
    
    $.ajax({
        url: issueResource,
        dataType: "json"
    }).fail(function(xhr, status, errorThrown ) {
        console.log("Error: " + errorThrown );
        console.log("Status: " + status );
        console.dir(xhr);
    }).done(function(jsonObject){
        console.log("SUCCESS");
        responseObject2 = jsonObject;
        issueArray = responseObject2.issues;
        var completed = 0;
        var toDo = 0;

        $.each(issueArray, function (index, issue) {
            if(issue.status == "Resolved" || issue.status == "Closed" || issue.status == "Done") {
                completed = completed + 1;
            } else {
                toDo = toDo + 1;
            }
        });
        barData.datasets[0].data[0] = issueArray.length
        barData.datasets[1].data[0]= completed;
        barChart.update();
    });
}
	





