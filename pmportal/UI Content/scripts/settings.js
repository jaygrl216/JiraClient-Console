var email=getCookie("email").toString();
$("#userText").val(getCookie("username").toString());
$("#urlText").val(getCookie("url").toString());
if (email!=""){
$("#emailInput").val(email);
};

function saveSettings(){
	var eaddress=$("#emailInput").val();
	var user=$("#userText").val();
	var baseURL=$("#urlText").val();
	var pass=$("#passText").val();
	var remember=$("#rememberBox").prop("checked");
	var hostURL=window.location.host;
	var testResource="http://"+hostURL+"/pmportal/rest/test/" + user + "/" + pass + "/" +baseURL;
	$.ajax({
		type:"GET",
		dataType:"text",
		url:testResource
	}).fail(function( xhr, status, errorThrown ) {
		console.log( "Error: " + errorThrown );
		console.log( "Status: " + status );
		console.dir( xhr );
		$("#fail").css("visibility","visible");
	}).done(function(response){
		if (response=="Success"){
			setCookie(user, pass, baseURL, remember);
			alert("Successfully saved credentials")
		}else{
			alert("Login failed!");
};
});
	settingsCookie(eaddress);
};