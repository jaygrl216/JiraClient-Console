var username=getCookie("username").toString();
var password=getCookie("password").toString();
var baseURL=getCookie("url").toString();
var hostURL = window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
var projectArray;
var overdue = 0;
var stop = 0;
var dateArray = new Array(0);
var averageSEA = 0;
var averageEEA = 0;



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

$(document).ajaxComplete(function() {
    $.each(projectArray, function (index, proj) {
        var dueDate = new Date();
            var dates = proj.due.split("-");
            dueDate.setMonth(dates[0]);
            dueDate.setDate(dates[1] - 1);
            dateArray = dateArray.concat(dueDate);  
    });
           
    console.log(dateArray);
    
    $('#calendar').empty();
    $('#calendar').fullCalendar({
        contentHeight: 'auto',
        dayRender: function(date, cell) {
            var date2 = date._d;
            for(var i = 0; i < dateArray.length; i++) {
                console.log(dateArray[i].getMonth());
                console.log(dateArray[i].getDate());
                 if(dateArray[i].getMonth() == date2.getMonth() && dateArray[i].getDate() == date2.getDate()) {
                    cell.css("background-color", "#9EF0AA");
                    console.log(dateArray[i]);
                    console.log(date._d);
                }
            }
            
        }
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

$(document).ajaxComplete(function(){
    $("graph2").append("<h4> Average SEA: " + averageSEA + "</h4>").append("<h4> Average EEA: " + averageEEA + "</h4>");
});

$(document).ready(function() {
    $('#calendar').append("Loading");
})

//function reRender(date, cell) {
//    var date2 = date._d;
//            for(var i = 0; i < dateArray.length; i++) {
//                console.log(dateArray[i].getMonth());
//                console.log(dateArray[i].getDate());
//                 if(dateArray[i].getMonth() == date2.getMonth() && dateArray[i].getDate() == date2.getDate()) {
//                    cell.css("background-color", "red");
//                    console.log(dateArray[i]);
//                    console.log(date._d);
//                }
//            }
//}



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
