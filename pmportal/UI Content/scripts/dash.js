var username = "jwashington";
var password = "Diamond2017";
var baseURL = "http://54.152.100.242/jira";
var homeResource = "http://localhost:8080/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var responseObject;

 var lineData = {
    labels: ['Italy', 'UK', 'USA', 'Germany', 'France', 'Japan'],
    datasets: [
        {
            label: '2010 customers #',
            backgroundColor: "rgba(250, 62, 116, 0.5)",
            borderColor: "rgba(250, 62, 116, 0.86)",
            data: [2500, 1902, 1041, 610, 1245, 952]
        },
        {
            label: '2014 customers #',
            backgroundColor: "rgba(75,192,192,0.4)",
            borderColor: "rgba(75,192,192,1)",
            data: [3104, 1689, 1318, 589, 1199, 1436]
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
    var ctx = document.getElementById('clients').getContext('2d');
    var lineChart = new Chart(ctx, {
    type: 'line',
    data: lineData,
    options: {
        maintainAspectRatio: false,
        responsive: true
        }
    });
});
    
  

