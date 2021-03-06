package com.stgo.security.monitor;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.net.action.CreateNewOrderAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stgo.security.monitor.util.SecurityQueryParameter;

/**
 * Security service REST API resource. Provide support for required handlers for statistic data.
 */
@Path("/security")
public class SecurityResource {
    public static final String APP_ZIP = "application/zip";

    private ProtectionOperator protectionOperator;
    /**
     * Associated class logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(SecurityResource.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final long ONE_DAY = 1000 * 60 * 60 * 24;

    /**
     * It's designed for and used by client side, time by time client will check with server to see if there's new
     * version. it's necessary, because 1/some time when server ask clients to update, client are closed. 2/when test
     * server can not reach to each computer behide the routers. so have to wait for them to reach to test server.
     * 
     * @return
     */
    @ResponseBody
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML, "application/zip",
            MediaType.APPLICATION_OCTET_STREAM })
    public Response reportStatus(
            @QueryParam(SecurityQueryParameter.VERSION)
            final String version,
            @QueryParam(SecurityQueryParameter.HOSTID)
            final String hostId,
            @QueryParam(SecurityQueryParameter.LABEL)
            final String label,
            @QueryParam(SecurityQueryParameter.MESSAGE)
            final String message,
            @QueryParam(SecurityQueryParameter.TIME)
            String time) {
        StreamingOutput output = protectionOperator.logStatus(version, hostId, label, message, time);
        return Response.ok(output).build();
    }

    /**
     * It's designed and used by server side. server side could call client side to lock and/or unlock a the protection.
     * 
     * @return
     */
    @Path("/lock")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Response setupServiceStatus() {
        ProtectionOperator.checkSystemSecurityStatus();
        return Response.ok("<stgo>system is protected, and under monitor. powered by stgo......</stgo>").build();
    }

    @Path("/unlock")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Response unlock(
            @QueryParam(SecurityQueryParameter.FUNCTION_ID)
            @DefaultValue(SecurityQueryParameter.DEFAULT_FUNCTION_ID)
            final int function_id,
            @QueryParam(SecurityQueryParameter.LIMIT)
            @DefaultValue(SecurityQueryParameter.DEFAULT_LIMIT)
            final int timeLimit,
            @QueryParam(SecurityQueryParameter.NAME)
            final String name,
            @QueryParam(SecurityQueryParameter.SN)
            final String sn) {
        protectionOperator.cancelUSBWriteProtection(name, sn, timeLimit, function_id);
        return Response
                .ok("<stgo>system is temporally unprotected, it will be under protection again after 1 minute. powered by stgo.......</stgo>")
                .build();

    }

    @Path("/checkOptionStatus")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Response checkOptionStatus() {
        List<OptionStatus> status = protectionOperator.returnCurrentOptionStatus();
        StringBuilder currentlyOpenedOptions = new StringBuilder();
        for (OptionStatus optionStatus : status) {
            currentlyOpenedOptions.append(optionStatus.getFunction_id());
        }
        return Response.ok("<stgo>" + currentlyOpenedOptions + "</stgo>").build();
    }

    @Path("/updateOptionStatus")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Response updateOptionStatus(
            @QueryParam(SecurityQueryParameter.FUNCTION_ID)
            @DefaultValue(SecurityQueryParameter.DEFAULT_FUNCTION_ID)
            final int function_id) {
        protectionOperator.updateOptionStatus(function_id);
        return Response.ok("<stgo>system is updated with " + function_id + "</stgo>").build();
    }

    /**
     * userd by server side, when server side updated, it will tell sub node to update. it's mainly used by server
     * inside the intranet, not the test server.
     * 
     * @param name
     * @param sn
     * @param inputFile
     * @return @Path("/upgrade")
     * @POST
     * @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML }) public Response upgrade(
     * @QueryParam(SecurityQueryParameter.NAME) final String name,
     * @QueryParam(SecurityQueryParameter.SN) final String sn, final File inputFile) { protectionOperator.upgrade(name,
     *                                        sn, inputFile);
     * 
     *                                        // return Response.ok(inputFile.getName()).build(); return
     *                                        Response.ok("<stgo>system is about to restart........</stgo>").build(); }
     */


    //==========================================================================================================
    
    /**
     * It's designed for and used by client side, time by time client will send to the server for the new orders.
     * it's necessary, because 
     * 1/some time user might edited the menu on servere side.
     * 2/when clicked the download button on tablet, should send request to server,
     *  !!!while if server is set as an IP address. then will send requeset to this ip and with a special string, 
     *  then the response will be treated differently.
     * if the host server of SanjiPos is set, then the menu should be modified on web. 
     * @return
     */
    @Path("/refreshMenu")
    @RequestMapping(headers = "Accept=application/json")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML, APP_ZIP,
            MediaType.APPLICATION_OCTET_STREAM })
    public Response refreshMenu(
    		@RequestBody String accountInfo) {
        System.out.println(accountInfo);
    	StreamingOutput output = protectionOperator.logStatus("version", "hostId", "label", "message", "time");
        return Response.ok(output).build();
    }

	/**
     * It's designed and used by tablet to send order. we suppose that tablet got menu from sanjiPos, so the menu SanjiPos must have.
     * But we should still check if the menu exist, in case the server deleted the dish while the tablet forgot to get.
     * currently tablet is already been able to synchronize menu from www.sharethegoodones.com.
     * while we can not leverage it, because it is synchronizing the whole database. so always one way, 
     * so always server->client if there's a server.
     * 
     * @TODO: infuture, will allow menu be added from any where, app synchronize to other places.
     * 
     * when tablet has serverIp set, then instead of sending to printer, while the send is clicked, it will send content to server.
     * server will save the record(generate bill and output) like what it did when get content from webpage order, then sanji will do print. 
     * @return
     */
    @Path("/newOrders")
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Response newOrders(@RequestBody String newOrders) {
    	CreateNewOrderAction.getInstance().processAddingOrderRequest(newOrders);
        return Response.ok("<stgo>system is protected, and under monitor. powered by stgo......</stgo>").build();
    }
    
    /**
     * Returns the <code>protectionOperator</code> value.
     *
     * @return The <code>protectionOperator</code>.
     */
    public ProtectionOperator getProtectionOperator() {
        return protectionOperator;
    }

    /**
     * Sets the <code>protectionOperator</code> value.
     *
     * @param protectionOperator
     *            The <code>protectionOperator</code> to set.
     */
    public void setProtectionOperator(
            ProtectionOperator protectionOperator) {
        this.protectionOperator = protectionOperator;
    }
}
