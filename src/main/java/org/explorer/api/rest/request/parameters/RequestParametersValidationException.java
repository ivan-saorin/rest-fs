package org.explorer.api.rest.request.parameters;

import org.explorer.api.exceptions.WebApiException;

public class RequestParametersValidationException extends WebApiException {

	private static final long serialVersionUID = -8284612346978985900L;

	public RequestParametersValidationException(String key) {
		super(String.format("Invalid request parameter [%s]", key));
	}

}
