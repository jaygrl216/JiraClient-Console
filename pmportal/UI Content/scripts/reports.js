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
        dayRender: function (date, cell) {
            var today = new Date();
            if (date.getDate() === today.getDate()) {
                cell.css("background-color", "red");
            }
        }
    })
});