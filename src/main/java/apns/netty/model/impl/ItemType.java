/*
 * ItemType.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.model.impl;

/**
 * The Enum ItemType.
 * @author arung
 */
public enum ItemType {

    /** The dev token. */
    DEV_TOKEN((byte) 1, 32),
    /** The payload. */
    PAYLOAD((byte) 2, 256),
    /** The not id. */
    NOT_ID((byte) 3, 4),
    /** The exp date. */
    EXP_DATE((byte) 4, 4),
    /** The prio. */
    PRIO((byte) 5, 1);

    /** The id. */
    private final Byte id;

    /** The size. */
    private final Integer size;

    /**
     * Instantiates a new item type.
     * @param id
     *            the id
     * @param size
     *            the size
     */
    ItemType(final Byte id, final Integer size) {
        this.id = id;
        this.size = size;

    }

    /**
     * Gets the id.
     * @return the id
     */
    public Byte getId() {
        return id;
    }

    /**
     * Gets the size.
     * @return the size
     */
    public Integer getSize() {
        return size;
    }
}
