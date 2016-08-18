var username=getCookie("username").toString();
var password=getCookie("password").toString();
var url=getCookie("url").toString();
//if (pm==""){
//	window.location="index.html";
//};
var baseURL=getCookie("url").toString();
var hostURL = window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var metrics = "http://"+hostURL+"/pmportal/rest/metrics/averages/" + username + "/" + password + "/" + baseURL;
//var config = "http://" +hostURL+"/pmportal/rest/config/get/user/" + pm+"/"+url;
var projectArray;
var overdue = 0;
var stop = 0;
var dateArray = new Array(0);
var averageSEA = 0;
var averageEEA = 0;
var eeaMin = 0.8;
var eeaMax = 1.25;
var seaMin = 0.8;
var seaMax = 1.25;
var date = "";
var week = "";
var current = new Date();

switch(current.getDay()) {
    case 0:
        week = week .concat("Sunday ");
        break;
    case 1:
        week = week.concat("Monday ");
        break;
    case 2:
        week = week.concat("Tuesday ");
        break;
    case 3:
        week = week.concat("Wednesday ");
        break;
    case 4:
        week = week.concat("Thursday ");
        break;
    case 5:
        week = week.concat("Friday ");
        break;
    case 6:
        week = week.concat("Saturday ");
        break;
}

switch(current.getMonth()) {
    case 0:
        date = date.concat("January ");
        break;
    case 1:
        date = date.concat("February ");
        break;
    case 2:
        date = date.concat("March ");
        break;
    case 3:
        date = date.concat("April ");
        break;
    case 4:
        date = date.concat("May ");
        break;
    case 5:
        date = date.concat("June ");
        break;
    case 6:
        date = date.concat("July ");
        break;
    case 7:
        date = date.concat("August ");
        break;
    case 8:
        date = date.concat("September ");
        break;
    case 9:
        date = date.concat("October");
        break;
    case 10:
        date = date.concat("November ");
        break;
    case 11:
        date = date.concat("December ");
        break;
}

date = date.concat(current.getDate() + ", 20" + (current.getYear() - 100));

$(document).ready(function(){
    $('#section5').append("<p>" + week + "<br>"+ date + "</p>");

    $('#calendar').fullCalendar({
        contentHeight: 'auto'
        });
});




$.ajax({
	url: homeResource,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
	projectArray = jsonObject.projects;

	$.each(projectArray, function (index, proj) {
		if(proj.overdue == true) {
            overdue = overdue + 1;
        }

	});
});

$.ajax({
	url: metrics,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
    averageSEA = jsonObject.aveSEA;
	averageEEA = jsonObject.aveEEA;
});

//$.ajax({
//	url: config,
//	dataType: "json"
//}).fail(function(xhr, status, errorThrown ) {
//	console.log("Error: " + errorThrown );
//	console.log("Status: " + status );
//	console.dir(xhr);
//}).done(function(jsonObject){
//    eeaMin = jsonObject.eeaMin;
//    eeaMax = jsonObject.eeaMax;
//    seaMin = jsonObject.seaMin;
//    seaMax = jsonObject.seaMax;
//});



$(document).ajaxStop(function () {
    averageSEA = Math.round(averageSEA * 100) / 100;
    averageEEA = Math.round(averageEEA * 100) / 100;

    if(stop == 0) {
        $('.gen1').hide();
        if(overdue == 0) {
            $("#section4reports").append("<p class='overdueGood'>" + overdue + "</p>");
        } else {
            $("#section4reports").append("<p class='overdueBad'>" + overdue + "</p>");
        }

        if(seaMin <= averageSEA && averageSEA <= seaMax) {
            $("#section2reports").append("<h5> Average SEA </h5> <p class='good'>" + averageSEA+ "</p>");
            $('.sea1').show();
        } else if (averageSEA > seaMax) {
            $("#section2reports").append("<h5> Average SEA </h5> <p class='bad'>" + averageSEA+ "</p>");
            $('.sea2').show();
        } else {
            $("#section2reports").append("<h5> Average SEA </h5> <p class='below'>" + averageSEA+ "</p>");
            $('.sea3').show();
        }

        if(seaMin <= averageEEA && averageEEA <= eeaMax) {
            $("#section2reports").append("<h5> Average EEA </h5> <p class='good'>" + averageEEA + "</p>");
            $('.eea1').show();
        } else if (averageEEA > eeaMax) {
            $("#section2reports").append("<h5> Average EEA </h5> <p class='bad'>" + averageEEA + "</p>");
            $('.eea2').show();
        } else {
           $("#section2reports").append("<h5> Average EEA </h5> <p class='below'>" + averageEEA + "</p>");
            $('.eea3').show();
        }

          $.each(projectArray, function (index, proj) {
            var dueDate = new Date();
            var dates = proj.due.split("-");
            dueDate.setMonth(dates[0]);
            dueDate.setDate(dates[1] - 1);
            dateArray = dateArray.concat(dueDate);
        });


    $('#calendar').fullCalendar('destroy');
    $('#calendar').fullCalendar({
        contentHeight: 'auto',
        dayRender: function(date, cell) {
            var date2 = date._d;
            for(var i = 0; i < dateArray.length; i++) {
                 if(dateArray[i].getMonth() == date2.getMonth() && dateArray[i].getDate() == date2.getDate()) {
                    cell.css("background-color", "#9EF0AA");
                }
            }

        }
    });
        stop = 1;
        }
});
