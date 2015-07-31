package com.elatier.bank.exceptions;

import javax.ws.rs.WebApplicationException;

/**
 * Created by kriaval on 31/07/15.
 */
public class InsufficientFundsException extends WebApplicationException {
    public InsufficientFundsException(String s) {
        super(s, 400);
    }

}
