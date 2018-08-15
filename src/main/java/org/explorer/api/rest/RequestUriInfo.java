package org.explorer.api.rest;

import javax.servlet.http.HttpServletRequest;

public class RequestUriInfo {

	private HttpServletRequest httpRequest;
	
	public RequestUriInfo(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public String getScheme() {
		return httpRequest.getScheme();		
	}
	
	public String getHost() {
		return httpRequest.getLocalAddr();		
	}

	public String getHostname() {
		return httpRequest.getServerName();		
	}

	public int getPort() {
		return httpRequest.getServerPort();		
	}

	public String getContextPath() {
		return httpRequest.getContextPath();		
	}
		
}
