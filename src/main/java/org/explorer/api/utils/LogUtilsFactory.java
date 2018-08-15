package org.explorer.api.utils;

import java.util.HashMap;

import org.slf4j.MDC;

public class LogUtilsFactory {
	
	private static LogUtilsFactory instance = new LogUtilsFactory();
	
	private static final int indentLimit = 10;
	private static final String template = "==";
	
	private static HashMap<String, LogUtils> cache = new HashMap<>();

	public class LogUtils {
		private int indentLimit = 10;
		private String template = "==";
		private String current = "";
		private int indent = 0;

		public LogUtils(int limit, String template) {
			this.indentLimit = limit;
			this.template = template;
		}
		
		public void push() {			
			//if (indent <= indentLimit) {
				MDC.put("indent", toString(++indent));
			//}
		}

		public void pop() {
			//if (indent >= 0) {
				MDC.put("indent", toString(--indent));
			//}
		}

		public int size() {
			return indent;
		}

		public void clear() {
			indent = 0;
		}
		
		public String toString(int indent) {
			//if (current.length() != (template.length() * sz)) {
			if (indent < 0) {
				indent = 0;
			}
				StringBuffer sb = new StringBuffer(indent * template.length());
				for (int i = 0; i < indent; i++) {
					sb.append(template);
				}
				if (sb.length() > 0) {
					sb.append("  ");
				}
				current = sb.toString();
			//}
			return current;
		}
		
	}

	public static LogUtils getUtils() {
		return getUtils("STANDARD");
	}
	
	public static LogUtils getUtils(Class<?> claxx) {
		return getUtils(claxx.getName());
	}

	public static LogUtils getUtils(String logger) {
		LogUtils utils = cache.get(logger);
		if (utils == null) {
			utils = instance.new LogUtils(indentLimit, template);
			cache.put(logger, utils);
		}
		return utils;
	}
	

	public static LogUtils getUtils(Class<?> claxx, String template) {
		return getUtils(claxx.getName(), template);
	}

	public static LogUtils getUtils(String logger, String template) {
		LogUtils utils = cache.get(logger);
		if (utils == null) {
			utils = instance.new LogUtils(indentLimit, template);
			cache.put(logger, utils);
		}
		return utils;
	}

	public static LogUtils getUtils(Class<?> claxx, String template, int indentLimit) {
		return getUtils(claxx.getName(), template, indentLimit);
	}

	public static LogUtils getUtils(String logger, String template, int indentLimit) {
		LogUtils utils = cache.get(logger);
		if (utils == null) {
			utils = instance.new LogUtils(indentLimit, template);
			cache.put(logger, utils);
		}
		return utils;
	}

	
}
