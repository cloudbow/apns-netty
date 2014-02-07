/*
 * FeedBackResponse.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.model.impl;

import java.sql.Timestamp;

/**
 * The Class FeedBackResponse.
 * @author arung
 */
public class FeedBackResponse {

    /** The device token. */
    private final String deviceToken;

    /** The last registered. */
    private Timestamp lastRegistered;

    /**
     * Instantiates a new feed back response.
     * @param timestamp
     *            the timestamp
     * @param deviceToken2
     *            the device token2
     */
    public FeedBackResponse(final Timestamp timestamp,
            final String deviceToken2) {
        deviceToken = deviceToken2;
        this.setLastRegistered(timestamp);

    }

    /**
     * Gets the device token.
     * @return the device token
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * Gets the last registered.
     * @return the last registered
     */
    public Timestamp getLastRegistered() {
        return lastRegistered;
    }

    /**
     * Sets the last registered.
     * @param lastRegistered
     *            the new last registered
     */
    public void setLastRegistered(final Timestamp lastRegistered) {
        this.lastRegistered = lastRegistered;
    }

}
