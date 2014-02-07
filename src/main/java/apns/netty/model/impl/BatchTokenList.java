/*
 * BatchTokenList.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.model.impl;

import java.util.ArrayList;
import java.util.List;

import apns.netty.model.ApnsBatchMessage;

/**
 * The Class BatchTokenList.
 * @author arung
 */
public class BatchTokenList implements ApnsBatchMessage {

    /** The token list. */
    private final List<String> tokenList = new ArrayList<String>();

    /** The apns message. */
    private ApnsMessage apnsMessage;

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#tokenList()
     */
    @Override
    public List<String> tokenList() {

        return tokenList;
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#messageList()
     */
    @Override
    public List<String> messageList() {

        return new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.ApnsBatchMessage#getApnsMessage()
     */
    @Override
    public ApnsMessage getApnsMessage() {
        return apnsMessage;
    }

    /**
     * Sets the apns message.
     * @param apnsMessage
     *            the new apns message
     */
    public void setApnsMessage(final ApnsMessage apnsMessage) {
        this.apnsMessage = apnsMessage;
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
