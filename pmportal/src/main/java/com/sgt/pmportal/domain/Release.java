package com.sgt.pmportal.domain;

import java.net.URI;
import org.joda.time.DateTime;

public class Release{
	protected String name;
	protected Long id;
	protected DateTime release;
	protected URI uri;
	
	public Release(String versionName, Long versionId, DateTime versionRelease, URI versionUri){    
		name=versionName;
		id=versionId;
		release=versionRelease;
		uri=versionUri;
	}

public String getName(){
	return name;
}
public Long getId() {
	return id;
}
public DateTime getRelease() {
	return release;
}
public URI getURI(){
	return uri;
}
}
