var uname = "amital";
var pass = "ComPuteR90";
var urlTemp = "http://54.152.100.242/jira";
setCookie(uname, pass, urlTemp);
//all the code above this line should be deleted in the final product
var username=getCookie("username").toString();
var password=getCookie("password").toString();
var baseURL=getCookie("url").toString();
var projKey = "DEV";
var hostURL=window.location.host;
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


 var barData = {
        labels: [],
        datasets: [
            {
                label: 'Issues Created',
                backgroundColor: "rgba(250, 62, 116, 0.5)",
                borderColor: "rgba(250, 62, 116, 0.86)",
                data: []
            },
            {
                label: 'Issues Completed',
                backgroundColor: "rgba(75,192,192,0.4)",
                borderColor: "rgba(75,192,192,1)",
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
                data: [],
                backgroundColor: [
                    "#F54747",
                    "#36A2EB",
                ],
                hoverBackgroundColor: [
                    "#FC3A3A",
                    "#36A2EB",
                ]
            }]
     };

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
    createBar();
    showInitialData();
});

$(document).ready(function(){
    $("#board").on( "click", "li" , function () {
        var item = $(this).text();
        $.each(projectArray, function (index, proj) {
            if(proj.name == item) {
                showProjectData(index);
            }
        });
    });
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
    var ctx = document.getElementById('progressGraph').getContext('2d');
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
    $("#graph1").append("<p> Project Name: " + project.name + "</p>").append
            ("<p> Project Key: " + project.key + "</p>").append
            ("<p> Project Lead: " + project.lead.displayName + "</p>").append
            ("<p> Release to Date: " + project.releases.length + "</p>");
    
    projKey = project.key;
    issueResource = "http://"+hostURL+"/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;
    metricResource = "http://"+hostURL+"/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;
	
	//this will pass the information to metrics, so when they click on the link, it continues with the same project
    $("#metricLink").attr("href", "metrics.html?project=" + projKey);
	
    $.ajax({
        url: metricResource,
        dataType: "json"
    }).fail(function(xhr, status, errorThrown ) {
        console.log("Error: " + errorThrown );
        console.log("Status: " + status );
        console.dir(xhr);
    }).done(function(jsonObject){
        console.log("SUCCESS");
        responseObject3 = jsonObject;
        metrics = responseObject3;
        pieData.datasets[0].data[0] = Math.round(metrics.progress * 100) / 100;
        pieData.datasets[0].data[1] = Math.round((100 - metrics.progress) * 100) / 100;
        createPie();
        $("#graph3").append("<p>" + metrics.projectedDate + "</p>");
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
    $("#graph1").empty();
    $("#graph1").append("<h4> Project Information </h4>").append("<p> Project Name: " + project.name + "</p>").append
            ("<p> Project Key: " + project.key + "</p>").append
            ("<p> Project Lead: " + project.lead.displayName + "</p>").append
            ("<p> Release to Date: " + project.releases.length + "</p>");
    $("#graph3").empty();
    
    projKey = project.key;
    issueResource = "http://"+hostURL+"/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;
    metricResource = "http://"+hostURL+"/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;
	
	//this will pass the information to metrics, so when they click on the link, it continues with the same project
    $("#metricLink").attr("href", "metrics.html?project=" + projKey);
	
    $.ajax({
        url: metricResource,
        dataType: "json"
    }).fail(function(xhr, status, errorThrown ) {
        console.log("Error: " + errorThrown );
        console.log("Status: " + status );
        console.dir(xhr);
    }).done(function(jsonObject){
        console.log("SUCCESS");
        responseObject3 = jsonObject;
        metrics = responseObject3;
        pieData.datasets[0].data[0] = Math.round(metrics.progress * 100) / 100;
        pieData.datasets[0].data[1] = Math.round((100 - metrics.progress) * 100) / 100;
        pieChart.update();
        $("#graph3").append("<h4> Projected End Date </h4>").append("<p>" + metrics.projectedDate + "</p>");
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
        barChart.update();
    });
};