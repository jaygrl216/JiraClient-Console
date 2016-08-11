function setCookie(uname, pass, url, pm){
		//the cookie will persist until 2038, unless deleted except password
		document.cookie ="username=" + uname + "; expires=Tuesday, January 18, 2038 01:00:00 AM";
		document.cookie="password=" + pass;
		document.cookie="url=" + url + "; expires=Tuesday, January 18, 2038 01:00:00 AM";
		document.cookie="pm="+pm+"; expires=Tuesday, January 18, 2038 01:00:00 AM";
};

function settingsCookie(emailAddress){
	document.cookie="email="+emailAddress + "; expires=Tuesday, January 18, 2038 01:00:00 AM";
};

function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for(var i = 0; i <ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length,c.length);
		}
	}
	return "";
}
