package org.explorer.api.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.net.MediaType;

public class NotAcceptableException extends WebApiException {

	private static final long serialVersionUID = 2535154260019836316L;
	
	private String accept;

	public NotAcceptableException(String accept) {
		super(
				"API",
				"CONFORMANCE",
				"405-999",
				String.format("Not acceptable MediaType: [%s]", accept));		
		this.accept = accept;
	}
	

	public NotAcceptableException(MediaType accept) {
		super(
				"API",
				"CONFORMANCE",
				"405-999",
				String.format("Not acceptable MediaType: [%s]", accept));		
		this.accept = accept.toString();
	}


	public String getAccept() {
		return accept;
	}
	
	@Override
	public JsonNode getJson() {
		ObjectNode rootNode = (ObjectNode) super.getJson();
		rootNode.set("accept", new TextNode(accept));
		return rootNode;
	}
	
}
