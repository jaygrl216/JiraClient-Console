var temp = location.search.substring(1).split("=");
var pm="";
if (temp.length>0){
	pm=temp[1];
};
var hostURL=window.location.host;
var responseObject;
var userArray;
var resource = "http://" + hostURL+ "/pmportal/rest/config/get/all/" + pm;
$.ajax({
	type : "GET",
	dataType : "text",
	url : resource
}).fail(function(xhr, status, errorThrown) {
	console.log("Error: " + errorThrown);
	console.log("Status: " + status);
	console.dir(xhr);
	alert("Failed to contact server!");
}).done(function(jsonObject){
	responseObject=jsonObject;
	userArray=responseObject.users;
	$.each(userArray, function (index, user) {
		var num = index + 1;
		$("#userList").append("<li>" + user.username + ", " + user.alias+"</li>");
	});
});
$(document).ready(function(){
	$("#userList").on( "click", "li" , function () {
		var item = $(this).text();
		$.each(userArray, function (index, user) {
			if(user.username + ", " + user.alias == item) {
				selectUser(user.username);
			};
		});
	});
});
function selectUser(username){
	var configResource="http://" + hostURL+ "/pmportal/rest/config/get/user/" + pm+"/"+username;
	$.ajax({
		type : "GET",
		dataType : "text",
		url : configResource
	}).fail(function(xhr, status, errorThrown) {
		console.log("Error: " + errorThrown);
		console.log("Status: " + status);
		console.dir(xhr);
		alert("Failed to contact server!");
	}).done(function(jsonObject){
		responseObject=jsonObject;
		var uname=responseObject.username;
		var pass=responseObject.password;
		var url=responseObject.url;
		setCookie(uname, pass, url, pm);
		window.location="home.html";
	});
};