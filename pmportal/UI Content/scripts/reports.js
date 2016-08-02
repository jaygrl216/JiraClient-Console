var username=getCookie("username").toString();
var password=getCookie("password").toString();
var baseURL=getCookie("url").toString();
var hostURL = window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var overdue = 0;
var stop = 0;
var dateArray = new Array(0);
var i = 0;



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
		if(proj.overdue == true) {
            overdue = overdue + 1;
        }
        
        var dueDate = new Date();
        var dates = proj.due.split("-");
        dueDate.setMonth(dates[0]);
        dueDate.setDate(dates[1]);
        dateArray[index] = dueDate;
        
	});
});


$(document).ajaxStop(function () {
    if(stop == 0) {
        if(overdue == 0) {       
            $("#graph").append("<p class='overdueGood'>" + overdue + "</p>");
        } else {
            $("#graph").append("<p class='overdueBad'>" + overdue + "</p>");
        }
        
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
        contentHeight: 'auto',
        dayRender: function (date, cell) {
            var date2 = date._d;
            if(dateArray[i].getMonth() == date2.getMonth() && dateArray[i].getDate() == date2.getDate()) {
                cell.css("background-color", "red");
                console.log(dateArray[i]);
                console.log(date._d);
            }
            i = i + 1;
        }
    })
});