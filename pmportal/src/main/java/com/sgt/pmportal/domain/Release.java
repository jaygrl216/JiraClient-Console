package com.sgt.pmportal.domain;

import java.net.URI;
import org.joda.time.DateTime;

/**
 * A release or version of a Jira project
 * 
 * @author Aman Mital
 * @author Jada Washington
 *
 */
public class Release{
	protected String name;
	protected Long id;
	protected DateTime releaseDate;
	protected URI uri;

	/**
	 * Constructor for a Release object
	 * 
	 * @param versionName
	 * @param versionId
	 * @param versionRelease
	 * @param versionUri
	 */
	public Release(String versionName, Long versionId, DateTime versionRelease, URI versionUri){    
		name=versionName;
		id=versionId;
		releaseDate=versionRelease;
		uri=versionUri;
	}

	public String getName(){
		return name;
	}
	public Long getId() {
		return id;
	}
	public DateTime getRelease() {
		return releaseDate;
	}
	public URI getURI(){
		return uri;
	}
}
