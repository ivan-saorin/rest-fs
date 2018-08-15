package org.explorer.api.rest.request.parameters;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.explorer.api.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestParameters {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestParameters.class);
	
	public class Holder {
		private HashMap <String, KeyValue> map;
		
		public Holder(HashMap <String, KeyValue> map) {
			this.map = map;
		}
		
		public Map <String, KeyValue> asMap() {
			return this.map;
		}
		
		public Collection <KeyValue> asCollection() {
			return this.map.values();
		}
		
	}
	
	private HashMap <String, KeyValue> attributes = new HashMap<String, KeyValue>();
	private HashMap <String, KeyValue> uriParameters = new HashMap<String, KeyValue>();
	private HashMap <String, KeyValue> headers = new HashMap<String, KeyValue>();
	private HashMap <String, KeyValue> queryStrings = new HashMap<String, KeyValue>();
	private HashMap <String, KeyValue> all = new HashMap<String, KeyValue>();
	private HttpServletRequest request;
	
	public RequestParameters(HttpServletRequest request, Map<String, String> uriParameters) {
		super();
		this.request = request;
		parseKeyValueTypes(uriParameters);
	}
	
	public RequestParameters(HttpServletRequest request) {
		super();
		this.request = request;
		parseKeyValueTypes(null);
	}

	private void parseKeyValueTypes(Map<String, String> uriParameters) {
		logger.info("======  parsedParameters:");
		parseAttributes();
		parseHeaders();		
		parseUriParameters(uriParameters);
		parseQueryStrings();
		putAll();
	}
	
	public void putAll() {	
		all.putAll(attributes);
		all.putAll(headers);
		all.putAll(uriParameters);
		all.putAll(queryStrings);
	}
	
	

	public void parseAttributes() {
		attributes.clear();
		Enumeration<String> names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Object value = request.getAttribute(name);
			//value = URLDecoder.decode(value, "UTF-8");
			attributes.put(name, new KeyValue(Type.ATTRIBUTE, name, value.toString()));
		}
	}

	public void parseHeaders() {
		headers.clear();
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = request.getHeader(name);
			//value = URLDecoder.decode(value, "UTF-8");
			headers.put(name, new KeyValue(Type.HEADER, name, value.toString()));
		}
	}

	public void parseUriParameters(Map<String, String> uriParameters) {
		this.uriParameters.clear();
		if (uriParameters == null) {
			String pathInfo = request.getPathInfo();
			String[] uriParams = pathInfo.split("/");
			int i = 0;
			for (String param : uriParams) {
				if (param.length() > 0) {
					String name = "uriParam" + (++i);
					this.uriParameters.put(name, new KeyValue(Type.URI_PARAMETER, name, param.toString()));
				}
			}
			
		}
		else {
			Set<String> keys = uriParameters.keySet();
			for (String name : keys) {
				String value = uriParameters.get(name);
				//value = URLDecoder.decode(value, "UTF-8");
				this.uriParameters.put(name, new KeyValue(Type.URI_PARAMETER, name, value.toString()));
			}
		}
	}

	public void parseQueryStrings() {
		queryStrings.clear();
		String queryString = request.getQueryString();
		logger.info("queryString=" + queryString);
		
		if (queryString != null) {
			String[] queries = queryString.split("&");
			
			for (String query : queries) {
				String[] a = query.split("=");
				String name = a[0];
				String value = a[1];
				try {
					value = URLDecoder.decode(value, "UTF-8");
					queryStrings.put(name, new KeyValue(Type.HEADER, name, value.toString()));
				} catch (UnsupportedEncodingException e) {
					// Does Nothing
					queryStrings.put(name, new KeyValue(Type.HEADER, name, a[1].toString()));
				}
				
			}
		}
	}
	
	/*
	public <T> void parseRequestBodyAttribute(T entity, String attributeName) {
		Object value = ReflectionUtils.getValue(entity, attributeName);
		logger.info("========  attributeName name:value > [{}]=[{}]", attributeName, value);
		this.add(new RequestParameter(RequestParameterType.BODY, attributeName, value.toString()));
	}
	*/
	
	public KeyValue get(String name) {
		return all.get(name);
	}
	
	public Holder all() {
		return new Holder(all);
	}
	
	public Holder filter(Type type) {
		switch (type) {
			case ATTRIBUTE: ; return new Holder(attributes);
			case HEADER: ; return new Holder(headers);
			case URI_PARAMETER: ; return new Holder(uriParameters);
			case QUERY_PARAMETER: ; return new Holder(queryStrings);
		case BODY:
			break;
		default:
			break;
		}
		return null;
	}	
	
	public boolean exists(String name) {
		return all.containsKey(name);
	}
	
	public boolean isEmpty() {
		return all.isEmpty();
	}

	public void add(KeyValue kvt) {
		all.clear();
		switch (kvt.getType()) {
			case ATTRIBUTE: attributes.put(kvt.getKey(), kvt); break;
			case HEADER: headers.put(kvt.getKey(), kvt); break;
			case URI_PARAMETER: uriParameters.put(kvt.getKey(), kvt); break;
			case QUERY_PARAMETER: queryStrings.put(kvt.getKey(), kvt); break;
			default: ;
		}		
		putAll();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.EOL);
		toString(sb, "uriParameters", uriParameters);
		toString(sb, "queryString", queryStrings);
		toString(sb, "headers", headers);
		toString(sb, "attributes", attributes);
		
		return sb.toString();
	}

	private void toString(StringBuilder sb, String title, Map<String, KeyValue> map) {
		sb.append(title).append(Constants.EOL);
		Set<String> keys = map.keySet();
		for (String key : keys) {
			KeyValue kv = map.get(key);
			sb.append("[").append(key).append("]=[").append(kv.getValue()).append("]").append(Constants.EOL);
		}
		sb.append(Constants.EOL);
	}
}