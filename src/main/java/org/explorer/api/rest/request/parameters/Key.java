package org.explorer.api.rest.request.parameters;

public class Key {

	protected Type type = Type.ATTRIBUTE;
	protected String key;

	public Key(Type type, String key) {
		super();
		this.type = type;
		this.key = key;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
}