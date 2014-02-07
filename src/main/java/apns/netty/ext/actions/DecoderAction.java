/*
 * DecoderAction.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.ext.actions;

import java.util.List;

import apns.netty.model.impl.FeedBackResponse;

/**
 * The Interface DecoderAction.
 * @author arung
 */
public interface DecoderAction {

    /**
     * After decode.
     * @param msg
     *            the msg
     */
    void afterDecode(List<FeedBackResponse> msg);
}
