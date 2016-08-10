var email=getCookie("email").toString();
$("#userText").val(getCookie("username").toString());
$("#urlText").val(getCookie("url").toString());
if (email!=""){
$("#emailInput").val(email);
};

var hostURL=window.location.host;
function saveSettings(){
	var eaddress=$("#emailInput").val();
	var user=$("#userText").val();
	var baseURL=$("#urlText").val();
	var pass=$("#passText").val();
	if (pass==""){
		pass=getCookie("password").toString();
	};
	var seaMin=$("#seaMin").val();
	var seaMax=$("#seaMax").val();
	var eeaMin=$("#eeaMin").val();
	var eeaMax=$("#eeaMax").val();
	var bugMax=$("#bugMax").val();
	var remember=$("#rememberBox").prop("checked");
	var testResource="http://"+hostURL+"/pmportal/rest/test/jira/" + user + "/" + pass + "/" +baseURL;
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
			settingsCookie(eaddress);
			saveToConfig(user, pass, baseURL, eaddress, seaMin, seaMax, eeaMin, eeaMax, bugMax);
		}else{
			alert("Login failed!");
};
});
};
function saveToConfig(user, pass, baseURL, eaddress, seaMin, seaMax, eeaMin, eeaMax, bugMax){
	var request="{\"username\":\"" + user+"\", \"password\":\""+ pass + "\", \"url\":\"" + baseURL+"\", \"email\":\"" + eaddress+"\", \"seaMin\":\""+seaMin+"\", \"seaMax\":\""+seaMax+"\", \"eeaMin\":\""+eeaMin+"\", \"eeaMax\":\""+eeaMax+"\", \"bugMax\":\""+bugMax+"\"}";
	var resource="http://"+hostURL+"/pmportal/rest/config/save";
	$.ajax({
		type:"POST",
		data:request,
		dataType:"text",
		url:resource
	}).fail(function( xhr, status, errorThrown ) {
		console.log( "Error: " + errorThrown );
		console.log( "Status: " + status );
		console.dir( xhr );
	}).done(function(response){
		alert("Successfully saved configuration");
});	
};
function testEmail(){
	var eaddress=$("#emailInput").val();
	var testResource="http://"+hostURL+"/pmportal/rest/test/email/"+eaddress;
	$.ajax({
		type:"GET",
		dataType:"text",
		url:testResource
	}).fail(function( xhr, status, errorThrown ) {
		console.log( "Error: " + errorThrown );
		console.log( "Status: " + status );
		console.dir( xhr );
		alert("Failed to reach server!")
	}).done(function(response){
		if (response=="Sent"){
			alert("An email has been sent to the specified address");
		}else{
			alert("Email failed to send!");
};
});
};
function resetSEA(){
	$("#seaMin").val("0.8");
	$("#seaMax").val("1.25");
};
function resetEEA(){
	$("#eeaMin").val("0.8");
	$("#eeaMax").val("1.25");
};
function resetBugs(){
	$("#bugMax").val("10");
};
