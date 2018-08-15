package org.explorer.api.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class ConflictException extends WebApiException {

	private static final long serialVersionUID = 1511245558163357139L;

	private String location;
	
	public ConflictException(String location) {
		super(
			"API",
			"RESOURCE",
			"409-999",
			"The request could not be completed due to a conflict with the current state of the resource.");
		this.location = location;
	}

	@Override
	public JsonNode getJson() {
		ObjectNode rootNode = (ObjectNode) super.getJson();
		rootNode.set("location", new TextNode(location));
		return rootNode;
	}
	

}
