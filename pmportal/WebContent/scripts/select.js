var temp = location.search.substring(1).split("=");
var pm="";
if (temp.length>0){
	pm=temp[1];
};
var host=window.location.host;
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
