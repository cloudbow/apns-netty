/*
 * ApnsMessage.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.model.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class ApnsMessage.
 * @author arung
 */
public class ApnsMessage {

    /** The item type to object map. */
    private final Map<ItemType, Object> itemTypeToObjectMap = new HashMap<ItemType, Object>();

    /**
     * Instantiates a new apns message.
     * @param builder
     *            the builder
     */
    public ApnsMessage(final Builder builder) {
        itemTypeToObjectMap.put(ItemType.DEV_TOKEN, builder.deviceToken);
        itemTypeToObjectMap.put(ItemType.PAYLOAD, builder.payLoad);
        itemTypeToObjectMap.put(ItemType.NOT_ID, builder.id);
        itemTypeToObjectMap.put(ItemType.EXP_DATE, builder.expTime);
        itemTypeToObjectMap.put(ItemType.PRIO, builder.prio);
    }

    /**
     * The Class Builder.
     */
    public static class Builder {

        /** The device token. */
        private String deviceToken;

        /** The pay load. */
        private String payLoad;

        /** The id. */
        private Integer id;

        /** The exp time. */
        private Integer expTime;

        /** The prio. */
        private Byte prio;

        /**
         * Builds the.
         * @return the apns message
         */
        public ApnsMessage build() {
            return new ApnsMessage(this);
        }

        /**
         * Device token.
         * @param devToken2
         *            the dev token2
         * @return the deviceToken
         */
        public Builder deviceToken(final String devToken2) {
            deviceToken = devToken2;
            return this;
        }

        /**
         * Pay load.
         * @param payLoad2
         *            the pay load2
         * @return the builder
         */
        public Builder payLoad(final String payLoad2) {
            this.payLoad = payLoad2;
            return this;
        }

        /**
         * Id.
         * @param id
         *            the id to set
         * @return the builder
         */
        public Builder id(final Integer id) {
            this.id = id;
            return this;
        }

        /**
         * Exp time.
         * @param expTime
         *            the expTime to set
         * @return the builder
         */
        public Builder expTime(final int expTime) {
            this.expTime = expTime;
            return this;
        }

        /**
         * Prio.
         * @param prio
         *            the prio to set
         * @return the builder
         */
        public Builder prio(final byte prio) {
            this.prio = prio;
            return this;
        }

    }

    // itemTypeToObjectMap.put(ItemType.DEV_TOKEN, builder.deviceToken);
    // itemTypeToObjectMap.put(ItemType.PAYLOAD, builder.payLoad);
    // itemTypeToObjectMap.put(ItemType.NOT_ID, builder.id);
    // itemTypeToObjectMap.put(ItemType.EXP_DATE, builder.expTime);
    // itemTypeToObjectMap.put(ItemType.PRIO, builder.prio);

    /**
     * Gets the device token.
     * @return the device token
     */
    public String getDeviceToken() {
        return (String) itemTypeToObjectMap.get(ItemType.DEV_TOKEN);
    }

    /**
     * Gets the pay load.
     * @return the pay load
     */
    public String getPayLoad() {
        return (String) itemTypeToObjectMap.get(ItemType.PAYLOAD);
    }

    /**
     * Gets the id.
     * @return the id
     */
    public String getId() {
        return (String) itemTypeToObjectMap.get(ItemType.NOT_ID);
    }

    /**
     * Gets the exp date.
     * @return the exp date
     */
    public Integer getExpDate() {
        return (Integer) itemTypeToObjectMap.get(ItemType.EXP_DATE);
    }

    /**
     * Gets the prio.
     * @return the prio
     */
    public Byte getPrio() {
        return (Byte) itemTypeToObjectMap.get(ItemType.PRIO);
    }

    /**
     * Gets the object.
     * @param itType
     *            the it type
     * @return the object
     */
    public Object getObject(final ItemType itType) {

        return itemTypeToObjectMap.get(itType);
    }
}
