package com.stgo.security.monitor.util;

/**
 * Class used to store security specific requests parameters
 */
public final class SecurityQueryParameter {
    //
    // Constants
    //
    public static final String LIMIT = "limit";
    public static final String FUNCTION_ID = "function_id";
    public static final String SN = "sn";
    public static final String NAME = "name";

    public static final String VERSION = "version";
    public static final String HOSTID = "hostId";
    public static final String LABEL = "label";
    public static final String MESSAGE = "message";
    public static final String TIME = "time";

    //
    // Default values
    //
    public static final String DEFAULT_FUNCTION_ID = "17";
    public static final String DEFAULT_LIMIT = "0";

    private SecurityQueryParameter() {
    }
}
