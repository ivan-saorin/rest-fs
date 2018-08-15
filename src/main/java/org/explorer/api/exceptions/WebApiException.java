package org.explorer.api.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class WebApiException extends Exception {
	
	private static final long serialVersionUID = -3288804441529137737L;

	protected ObjectMapper mapper = new ObjectMapper();
	private String layer = "API";
	private String category = "GENERIC";
	private String code = "99999999";
	private String description = "Unknown Error";
	
	public WebApiException() {
		super();
	}

	public WebApiException(String message) {
		super(message);
		this.description = message;
	}

	public WebApiException(Throwable cause) {
		super(cause);
		this.description = cause.getMessage();
	}

	public WebApiException(String message, Throwable cause) {
		super(message, cause);
		this.description = message;
	}

	public WebApiException(String layer, String category, String code, String description, String message) {
		super(message);
		this.layer = layer;
		this.category = category;
		this.code = code;
		this.description = description;
	}

	public WebApiException(String layer, String category, String code, String description, String message, Throwable t) {
		super(message, t);
		this.layer = layer;
		this.category = category;
		this.code = code;
		this.description = description;		
	}

	public WebApiException(String layer, String category, String code, String description) {
		super(description);
		this.layer = layer;
		this.category = category;
		this.code = code;
		this.description = description;		
	}

	public String getLayer() {
		return layer;
	}

	public String getCategory() {
		return category;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public JsonNode getJson() {
		ObjectNode root = mapper.createObjectNode();
		root.set("layer", new TextNode(layer));
		root.set("category", new TextNode(category));
		root.set("code", new TextNode(code));
		root.set("description", new TextNode(description));
		Throwable cause = getCause();
		if (cause != null) {
			root.set("cause", new TextNode(cause.getClass().getName() + ": " + cause.getMessage()));
		}
		return root;
	}

	public void rethrowAsRuntimeException() {
		throw new WebApiRuntimeException(this);		
	}
	
}
