package com.stgo.security.monitor.dao;

import javax.ws.rs.WebApplicationException;

public class InvalidQueryException extends WebApplicationException {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2892323236521415835L;

    /**
     * @param messages
     *            error message(s)
     */
    public InvalidQueryException(final String messages) {
        super(messages);
    }

    public InvalidQueryException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param cause
     *            underlying cause of the error
     * @param messages
     *            error message(s)
     */
    public InvalidQueryException(final Throwable cause, final String messages) {
        super(messages, cause, 0);
    }

}
