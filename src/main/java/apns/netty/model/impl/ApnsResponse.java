/*
 * ApnsResponse.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.model.impl;

/**
 * The Class ApnsResponse.
 * @author arung
 */
public class ApnsResponse {

    /** The command. */
    short command;

    /** The status. */
    short status;

    /** The id. */
    int id;

    /**
     * Gets the command.
     * @return the command
     */
    public short getCommand() {
        return command;
    }

    /**
     * Sets the command.
     * @param command
     *            the command to set
     */
    public void setCommand(final short command) {
        this.command = command;
    }

    /**
     * Gets the status.
     * @return the status
     */
    public short getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status
     *            the status to set
     */
    public void setStatus(final short status) {
        this.status = status;
    }

    /**
     * Gets the id.
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id
     *            the id to set
     */
    public void setId(final int id) {
        this.id = id;
    }

}
