/*
 * This file is part of OTSP.
 * (C) 2011-2011 - Open Text Corporation
 * All rights reserved.
 */
package com.stgo.security.server;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

/**
 * Test resource to expose in server.
 */
@Path("")
public class TheTestResource2 {
    /**
     * XML RPC needs to work with byte arrays instead of Strings, so this is
     * delegating to doTest(String).
     * 
     * @param arg
     *            Input argument as byte array.
     * @return The input argument, prefixed with "test2 - ", as byte array.
     */
    public byte[] doTest(final byte[] arg) {
        return doTest(new String(arg)).getBytes();
    }

    /**
     * Returns the input argument, prefixed with "test2 - ".
     * 
     * @param arg
     *            Input argument.
     * @return The input argument, prefixed with "test2 - ".
     */
    @POST
    public String doTest(final String arg) {
        try {
            final String val = "test2 - " + arg;
            System.err.println("2-Returning " + val);
            return val;
        } catch (final Throwable t) {
            throw new WebApplicationException(t);
        }
    }
}
