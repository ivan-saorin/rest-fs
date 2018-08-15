package org.explorer.api.rest.request.parameters;

import java.util.Collection;

public class KeyInfo {
	private Type type = Type.ATTRIBUTE;
	private String key;
	private String dataType;
	private String format;
	private String pattern;
	private int minLength = -1;
	private int maxLength = -1;
	private Collection<String> enumeration;
	private boolean required;
	
	public KeyInfo(Type type, String key, String dataType, String format, String pattern, int minLength, int maxLength, Collection<String> enumeration, boolean required) {
		super();
		this.type = type;
		this.key = key;
		this.dataType = dataType;
		this.format = format;
		this.pattern = pattern;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.enumeration = enumeration;
		this.required = required;
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

	public String getDataType() {
		return dataType;
	}

	public String getFormat() {
		return format;
	}

	public String getPattern() {
		return pattern;
	}

	public int getMinLength() {
		return minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public Collection<String> getEnumeration() {
		return enumeration;
	}

	public boolean isRequired() {
		return required;
	}

	@Override
	public String toString() {
		return String.format("%s name:value > [%s]:[%s]}", this.getClass().getName(), this.key);
	}

	public boolean hasDataType() {
		return (dataType != null);
	}

	public boolean hasFormat() {
		return (format != null);
	}
	
	public boolean hasPattern() {
		return (pattern != null);
	}

	public boolean hasMinLength() {
		return (minLength > -1);
	}

	public boolean hasMaxLength() {
		return (maxLength > 0);
	}

}
