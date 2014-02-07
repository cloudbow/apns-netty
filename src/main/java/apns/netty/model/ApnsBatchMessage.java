/*
 * ApnsBatchMessage.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.model;

import java.util.List;

import apns.netty.model.impl.ApnsMessage;

/**
 * The Interface ApnsBatchMessage.
 * @author arung
 */
public interface ApnsBatchMessage {

    /**
     * Token list.
     * @return the list
     */
    List<String> tokenList();

    /**
     * Message list.
     * @return the list
     */
    List<String> messageList();

    /**
     * Gets the apns message.
     * @return the apns message
     */
    ApnsMessage getApnsMessage();

    /**
     * Invariant messages.
     * @return the list
     */
    public abstract List<ApnsMessage> invariantMessages();

    /**
     * Update batch position.
     * @param notId
     *            the not id
     * @param index
     *            the index
     */
    public abstract void updateBatchPosition(final Integer notId,
            final Integer index);

}
