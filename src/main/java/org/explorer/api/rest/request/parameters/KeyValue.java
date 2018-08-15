package org.explorer.api.rest.request.parameters;

public class KeyValue extends Key {
	private String value;
	
	public KeyValue(Type type, String key, String value) {
		super(type, key);
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("%s name:value > [%s]:[%s]}", this.getClass().getName(), this.key, this.value);
	}

	public boolean isMissingValue() {		
		return ((getValue() == null) || ("".equals(getValue())));
	}
}