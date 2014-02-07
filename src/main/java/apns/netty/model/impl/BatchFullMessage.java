/*
 * BatchFullMessage.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apns.netty.model.ApnsBatchMessage;

/**
 * The Class BatchFullMessage.
 * @author arung
 */
public class BatchFullMessage implements ApnsBatchMessage {

    /** The inv messages. */
    List<ApnsMessage> invMessages = new ArrayList<ApnsMessage>();

    /** The id to index map. */
    private final Map<Integer, Integer> idToIndexMap = new HashMap<Integer, Integer>();

    /**
     * Adds the inv messages.
     * @param msg
     *            the msg
     */
    public void addInvMessages(final ApnsMessage msg) {
        invMessages.add(msg);
    }

    /**
     * Slice and store messages.
     * @param identifier
     *            the identifier
     */
    public void sliceAndStoreMessages(final int identifier) {

        final int toIndex = idToIndexMap.get(identifier) - 1;
        if (toIndex > 0) {
            invMessages = new ArrayList<ApnsMessage>(invariantMessages()
                    .subList(toIndex + 1, invMessages.size()));
        } else {
            invMessages = new ArrayList<ApnsMessage>();
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * apns.netty.model.ApnsBatchMessage#updateBatchPosition(java.lang.Integer,
     * java.lang.Integer)
     */
    @Override
    public void updateBatchPosition(final Integer notId, final Integer index) {
        idToIndexMap.put(notId, index);
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#tokenList()
     */
    @Override
    public List<String> tokenList() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#messageList()
     */
    @Override
    public List<String> messageList() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.ApnsBatchMessage#invariantMessages()
     */
    @Override
    public List<ApnsMessage> invariantMessages() {
        return invMessages;
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.model.BatchApnsMessage#getApnsMessage()
     */
    @Override
    public ApnsMessage getApnsMessage() {
        // TODO Auto-generated method stub
        return null;
    }

}
