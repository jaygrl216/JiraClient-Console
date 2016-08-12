var username=getCookie("username").toString();
var password=getCookie("password").toString();
var url=getCookie("url").toString();
var pm=getCookie("pm").toString();
if (pm==""){
	window.location="index.html";
};
var baseURL=getCookie("url").toString();
var hostURL = window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var metrics = "http://"+hostURL+"/pmportal/rest/metrics/averages/" + username + "/" + password + "/" + baseURL;
var config = "http://" +hostURL+"/pmportal/rest/config/get/user/" + pm+"/"+url;
var projectArray;
var overdue = 0;
var stop = 0;
var dateArray = new Array(0);
var averageSEA = 0;
var averageEEA = 0;
var eeaMin = 0;
var eeaMax = 0;
var seaMin = 0;
var seaMax = 0;



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

$.ajax({
	url: config,
	dataType: "json"
}).fail(function(xhr, status, errorThrown ) {
	console.log("Error: " + errorThrown );
	console.log("Status: " + status );
	console.dir(xhr);
}).done(function(jsonObject){
    eeaMin = jsonObject.eeaMin;
    eeaMax = jsonObject.eeaMax;
    seaMin = jsonObject.seaMin;
    seaMax = jsonObject.seaMax;
});



$(document).ajaxStop(function () {
    averageSEA = Math.round(averageSEA * 100) / 100;
    averageEEA = Math.round(averageEEA * 100) / 100;

    if(stop == 0) {
        $('.gen1').hide();
        if(overdue == 0) {
            $("#graph").append("<p class='overdueGood'>" + overdue + "</p>");
        } else {
            $("#graph").append("<p class='overdueBad'>" + overdue + "</p>");
        }

        if(seaMin <= averageSEA && averageSEA <= seaMax) {
            $("#graph2").append("<h5> Average SEA </h5> <p class='good'>" + averageSEA+ "</p>");
            $('.sea1').show();
        } else if (averageSEA > seaMax) {
            $("#graph2").append("<h5> Average SEA </h5> <p class='bad'>" + averageSEA+ "</p>");
            $('.sea2').show();
        } else {
            $("#graph2").append("<h5> Average SEA </h5> <p class='below'>" + averageSEA+ "</p>");
            $('.sea3').show();
        }

        if(seaMin <= averageEEA && averageEEA <= eeaMax) {
            $("#graph2").append("<h5> Average EEA </h5> <p class='good'>" + averageEEA + "</p>");
            $('.eea1').show();
        } else if (averageEEA > eeaMax) {
            $("#graph2").append("<h5> Average EEA </h5> <p class='bad'>" + averageEEA + "</p>");
            $('.eea2').show();
        } else {
           $("#graph2").append("<h5> Average EEA </h5> <p class='below'>" + averageEEA + "</p>");
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


$(document).ready(function() {
    $('#calendar').fullCalendar({
        contentHeight: 'auto'
    });

});




//	$(document).ready(function() {
//		
//		$('#calendar').fullCalendar({
//			header: {
//				left: 'prev,next today',
//				center: 'title',
//				right: 'month,agendaWeek,agendaDay'
//			},
//			defaultDate: '2016-06-12',
//			editable: true,
//			eventLimit: true, // allow "more" link when too many events
//			events: [
//				{
//					title: 'All Day Event',
//					start: '2016-06-01'
//				},
//				{
//					title: 'Long Event',
//					start: '2016-06-07',
//					end: '2016-06-10'
//				},
//				{
//					id: 999,
//					title: 'Repeating Event',
//					start: '2016-06-09T16:00:00'
//				},
//				{
//					id: 999,
//					title: 'Repeating Event',
//					start: '2016-06-16T16:00:00'
//				},
//				{
//					title: 'Conference',
//					start: '2016-06-11',
//					end: '2016-06-13'
//				},
//				{
//					title: 'Meeting',
//					start: '2016-06-12T10:30:00',
//					end: '2016-06-12T12:30:00'
//				},
//				{
//					title: 'Lunch',
//					start: '2016-06-12T12:00:00'
//				},
//				{
//					title: 'Meeting',
//					start: '2016-06-12T14:30:00'
//				},
//				{
//					title: 'Happy Hour',
//					start: '2016-06-12T17:30:00'
//				},
//				{
//					title: 'Dinner',
//					start: '2016-06-12T20:00:00'
//				},
//				{
//					title: 'Birthday Party',
//					start: '2016-06-13T07:00:00'
//				},
//				{
//					title: 'Click for Google',
//					url: 'http://google.com/',
//					start: '2016-06-28'
//				}
//			]
//		});
//		
//	});
