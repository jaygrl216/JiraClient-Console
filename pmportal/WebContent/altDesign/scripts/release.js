//var username=getCookie("username").toString();
//var password=getCookie("password").toString();
//var baseURL=getCookie("url").toString();
//var hostURL = window.location.host;
//var homeResource = "http://"+hostURL+"/pmportal/rest/home/" + username + "/" + password + "/" + baseURL;
//var issueResource = "http://"+hostURL+"/pmportal/rest/issues/" + projKey + "/" + username + "/" + password + "/" + baseURL;
//var metricResource = "http://"+hostURL+"/pmportal/rest/metrics/project/basic/" + projKey + "/" + username + "/" + password + "/" + baseURL;

var current = new Date();

$(document).ready(function(){
    $('#section5').append(current.getDate);
});
