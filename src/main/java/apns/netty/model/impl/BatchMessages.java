/*
 * BatchMessages.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.model.impl;

import java.util.ArrayList;
import java.util.List;

import apns.netty.model.ApnsBatchMessage;

/**
 * The Class BatchMessages.
 * @author arung
 */
public class BatchMessages implements ApnsBatchMessage {

    /** The batch messages. */
    List<String> batchMessages = new ArrayList<String>();

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#tokenList()
     */
    @Override
    public List<String> tokenList() {

        return new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#messageList()
     */
    @Override
    public List<String> messageList() {

        return batchMessages;
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#getApnsMessage()
     */
    @Override
    public ApnsMessage getApnsMessage() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#invariantMessages()
     */
    @Override
    public List<ApnsMessage> invariantMessages() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * apns.netty.model.ApnsBatchMessage#updateBatchPosition(java.lang.Integer,
     * java.lang.Integer)
     */
    @Override
    public void updateBatchPosition(final Integer notId, final Integer index) {
    }

}
