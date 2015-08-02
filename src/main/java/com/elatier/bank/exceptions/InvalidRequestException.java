package com.elatier.bank.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by kriaval on 02/08/15.
 */
public class InvalidRequestException extends WebApplicationException {

    public InvalidRequestException(int code, String message) {
        super(Response.status(code).
                entity(new JSONExceptionMessageContainer(message)).type(MediaType.APPLICATION_JSON).build());
    }
}