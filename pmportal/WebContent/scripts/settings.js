var email=getCookie("email").toString();
if (email!=""){
	$("#emailInput").val(email);
};
var pm=getCookie("pm").toString();
if (pm==""){
	window.location="index.html";
};
var hostURL=window.location.host;
function saveSettings(){
	var eaddress=$("#emailInput").val();
	var jname=$("#userText").val();
	var baseURL=$("#urlText").val();
	var jpass=$("#passText").val();
	var alias=$("#alias").val();
	var seaMin=$("#seaMin").val();
	var seaMax=$("#seaMax").val();
	var eeaMin=$("#eeaMin").val();
	var eeaMax=$("#eeaMax").val();
	var bugMax=$("#bugMax").val();
	var testResource="http://"+hostURL+"/pmportal/rest/test/jira/" + jname + "/" + jpass + "/" +baseURL;
	if (jname!=""){
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
				setCookie(jname, jpass, baseURL, remember);
				settingsCookie(eaddress);
				saveToConfig(jname, jpass, baseURL, eaddress, alias);
				saveBounds(seaMin, seaMax, eeaMin, eeaMax, bugMax);
			}else{
				alert("Login failed!");
			};
		});
	}else{
		saveToConfig(jname, jpass, baseURL, eaddress, alias);
		saveBounds(seaMin, seaMax, eeaMin, eeaMax, bugMax);
	}
};
function saveToConfig(jname, jpass, baseURL, eaddress, alias){
	var updateRequest = "{\"pm\":\"" + pm + "\", \"password\":\""
	+ "" + "\", \"email\":\"" + eaddress + "\"}";
	var saveRequest="{\"pm\":\"" + pm + "\", \"username\":\"" +jname+"\", \"password\":\""+ jpass + "\", \"url\":\"" + baseURL+"\", \"alias\":\"" + alias+"\", \"seaMin\":\""+0.8+"\", \"seaMax\":\""+1.25+"\", \"eeaMin\":\""+0.8+"\", \"eeaMax\":\""+1.25+"\", \"bugMax\":\""+10+"\"}";
	var updateResource = "http://" + hostURL + "/pmportal/rest/config/save/update";
	var saveResource = "http://" + hostURL + "/pmportal/rest/config/save";
	$.ajax({
		type : "POST",
		data : updateRequest,
		dataType : "text",
		url : updateResource
	}).fail(function(xhr, status, errorThrown) {
		console.log("Error: " + errorThrown);
		console.log("Status: " + status);
		console.dir(xhr);
	}).done(function(response) {
		if  (jname!=""){
			$.ajax({
				type : "POST",
				data : saveRequest,
				dataType : "text",
				url : saveResource
			}).fail(function(xhr, status, errorThrown) {
				console.log("Error: " + errorThrown);
				console.log("Status: " + status);
				console.dir(xhr);
			}).done(function(response) {
				alert("Successfully saved new user");
			});
		}
	});
};

//the bounds are saved for the current jira user
function saveBounds(seaMin, seaMax, eeaMin, eeaMax, bugMax){
	var username=getCookie("username");
	var password=getCookie("password");
	var getResource="http://"+hostURL+"/pmportal/rest/config/get/user/"+pm+"/"+username;
	var saveResource = "http://" + hostURL + "/pmportal/rest/config/save";
	//Get the current user's data, then update it
	$.ajax({
		type:"GET",
		dataType:"json",
		url:getResource
	}).fail(function( xhr, status, errorThrown ) {
		console.log( "Error: " + errorThrown );
		console.log( "Status: " + status );
		console.dir( xhr );
	}).done(function(jsonObject){
		var url=jsonObject.url;
		var alias=jsonObject.url;
		var saveRequest="{\"pm\":\"" + pm + "\", \"username\":\"" +username+"\", \"password\":\""+ password + "\", \"url\":\"" + url+"\", \"alias\":\"" + alias+"\", \"seaMin\":\""+seaMin+"\", \"seaMax\":\""+seaMax+"\", \"eeaMin\":\""+eeaMin+"\", \"eeaMax\":\""+eeaMax+"\", \"bugMax\":\""+bugMax+"\"}";
		$.ajax({
			type : "POST",
			data : saveRequest,
			dataType : "text",
			url : saveResource
		}).fail(function(xhr, status, errorThrown) {
			console.log("Error: " + errorThrown);
			console.log("Status: " + status);
			console.dir(xhr);
		});
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
