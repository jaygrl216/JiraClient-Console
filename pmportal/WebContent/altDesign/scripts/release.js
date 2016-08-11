//var username=getCookie("username").toString();
//var password=getCookie("password").toString();
//var baseURL=getCookie("url").toString();
//var hostURL = window.location.host;
//var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
//var issueResource = "http://"+hostURL+"/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;
//var metricResource = "http://"+hostURL+"/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;

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
});
