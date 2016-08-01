var username=getCookie("username").toString();
var password=getCookie("password").toString();
var baseURL=getCookie("url").toString();
var hostURL = window.location.host;
var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;

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
            var today = new Date();
            today.setMonth(7);
            today.setDate(14);
            
            var date2 = date._d;
            if(today.getMonth() == date2.getMonth() && today.getDate() == date2.getDate()) {
                cell.css("background-color", "red");
                console.log(cell);
                console.log(today);
                console.log(date._d);
            }
            
        }
    })
});