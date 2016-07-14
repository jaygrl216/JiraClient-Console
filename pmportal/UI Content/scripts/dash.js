var username = "jwashington";
var password = "Diamond2017";
var baseURL = "http://54.152.100.242/jira";
var homeResource = "http://localhost:8080/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var issueResource = "http://localhost:8080/pmportal/rest/issues" + projKey + "/" + username + "/" + password + "/" + baseURL;
var responseObject;

 var barData = {
    labels: [],
    datasets: [
        {
            label: 'Isscues Created',
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
            data: [80, 20],
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
    console.log("SUCCESS");
	responseObject = jsonObject;
});

$(document).ajaxStop(function () {
    var projectArray;
    projectArray = responseObject.projects;
    console.log(projectArray.length);

     $.each(projectArray, function (index, proj) {
        var num = index + 1;
        $("#projectList").append("<li>" + proj.name +  "</li>");
        console.log(proj);
        });
      $("#projectList li").click(function(){
        console.log("Project was clicked");
    });
    
    $("#graph1").append("<h5> Project Name: " + projectArray[0].name + "</h5>").append
        ("<h5> Project Lead: " + projectArray[0].lead.displayName + "</h5>");
});

$(document).ready(function() {
    $.ajax({
        url: issueResource,
        dataType: "json"
    }).fail(function(xhr, status, errorThrown ) {
        console.log("Error: " + errorThrown );
        console.log("Status: " + status );
        console.dir(xhr);
    }).done(function(jsonObject){
        console.log("SUCCESS");
        responseObject = jsonObject;
    });
    
    var ctx = document.getElementById('issues').getContext('2d');
    var barChart = new Chart(ctx, {
    type: 'bar',
    data: barData,
    options: {
        maintainAspectRatio: false,
        responsive: true
        }
    });
    
    var ctx = document.getElementById('progressGraph').getContext('2d');
    var pieChart = new Chart(ctx, {
    type: 'pie',
    data: pieData,
    options: {
        maintainAspectRatio: true,
        responsive: true
        }
    });
});
    
  

