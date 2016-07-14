var username = "jwashington";
var password = "Diamond2017";
var baseURL = "http://54.152.100.242/jira";
var projKey = "PMPOR";
var homeResource = "http://localhost:8080/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var issueResource = "http://localhost:8080/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;
var responseObject;
var responseObject2;
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
        var projectArray = responseObject.projects;

         $.each(projectArray, function (index, proj) {
            var num = index + 1;
            $("#projectList").append("<li>" + proj.name +  "</li>");
            });

        $("#graph1").append("<h5> Project Name: " + projectArray[0].name + "</h5>").append
            ("<h5> Project Key: " + projectArray[0].key + "</h5>").append
            ("<h5> Project Lead: " + projectArray[0].lead.displayName + "</h5>")
        var issueArray = responseObject2.issues;
        var completed = 0;
        var toDo = 0;

        $.each(issueArray, function (index, issue) {
            if(issue.status == "Resolved" || issue.status == "Closed") {
                completed = completed + 1;
            }
        });
        barData.datasets[0].data[0] = issueArray.length;
        barData.datasets[1].data[0] = completed;
    });

$(document).ajaxStop(function () {
    createBar();
    createPie();
});


function createBar() {
    var ctx = document.getElementById('issues').getContext('2d');
    var barChart = new Chart(ctx, {
    type: 'bar',
    data: barData,
    options: {
        maintainAspectRatio: false,
        responsive: true,
        scaleOverride:true,
        scaleSteps:9,
        scaleStartValue:0,
        scaleStepWidth:100
        }
        
    }); 
}

function createPie() {
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
    
    var ctx = document.getElementById('progressGraph').getContext('2d');
    var pieChart = new Chart(ctx, {
    type: 'pie',
    data: pieData,
    options: {
        maintainAspectRatio: true,
        responsive: true
        }
    });
}



















 






$(document).ready(function() {
   
    
});
    
  

