package org.explorer.api.exceptions;

public class ParsingException extends WebApiException {


	private static final long serialVersionUID = -6849157195374594525L;

	public ParsingException(String message) {
		super(
			"API",
			"PARSER",
			"400-999",
			"The server cannot deal with the request made by the client.");
	}

	public ParsingException(String message, Throwable cause) {
		super(			
			"API",
			"PARSER",
			"400-999",
			"The server cannot deal with the request made by the client.",
			message, cause);
	}

	public ParsingException(Throwable cause) {
		super(			
			"API",
			"PARSER",
			"400-999",
			"The server cannot deal with the request made by the client.",
			"", cause);
	}

}
