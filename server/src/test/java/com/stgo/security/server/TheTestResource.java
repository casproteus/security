/*
 * This file is part of OTSP.
 * (C) 2011-2011 - Open Text Corporation
 * All rights reserved.
 */
package com.stgo.security.server;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.io.IOUtils;
import org.mule.DefaultMuleMessage;
import org.mule.RequestContext;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

/**
 * Test resource to expose in server.
 */
@Path("")
public class TheTestResource {
    /**
     * XML RPC needs to work with byte arrays instead of Strings, so this is
     * delegating to doTest(String).
     * 
     * @param arg
     *            Input argument as byte array.
     * @return The input argument, prefixed with "test1 - " and the result of
     *         calling test2, as byte array.
     */
    public byte[] doTest(final byte[] arg) {
        return doTest(new String(arg)).getBytes();
    }

    /**
     * File transport needs to work with InputStream instead of Strings, so this is
     * delegating to doTest(String).
     * 
     * @param arg
     *            Input argument as InputStream.
     * @return The input argument, prefixed with "test1 - " and the result of
     *         calling test2, as byte array.
     * @throws IOException 
     */
    public byte[] doTest(final InputStream arg) throws IOException {
        return doTest(IOUtils.toString(arg, "UTF-8")).getBytes();
    }

    /**
     * Returns the input argument, prefixed with "test1 - " and the result of
     * calling test2.
     * 
     * @param arg
     *            Input argument.
     * @return The input argument, prefixed with "test1 - " and the result of
     *         calling test2.
     */
    @POST
    public String doTest(final String arg) {
        try {
            final MuleEventContext context = RequestContext.getEventContext();
            final MuleMessage message = new DefaultMuleMessage(arg,
                    context.getMuleContext());
            final MuleMessage response = context.sendEvent(message, "test2");
            final String val = "test1 - " + response.getPayloadAsString()
                    + " - " + arg;
            System.err.println("1-Returning " + val);
            return val;
        } catch (final Throwable t) {
            throw new WebApplicationException(t);
        }
    }
}
