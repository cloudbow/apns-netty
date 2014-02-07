/*
 * SingleMessageQueue.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.queues.single;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;
import apns.netty.model.impl.ApnsMessage;

/**
 * The Class SingleMessageQueue.
 * @author arung
 */
@Component
public class SingleMessageQueue extends LinkedBlockingQueue<ApnsMessage> {

    /** The Constant INTERRUPTED_BY_SOME_ONE. */
    private static final String INTERRUPTED_BY_SOME_ONE = "Interrupted by some one";

    /** The logger. */
    Logger logger = Logger.getLogger(SingleMessageQueue.class);

    /** The apns config. */
    @Autowired
    ApnsConfig apnsConfig;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new single message queue.
     * @param apnsConfig
     *            the apns config
     */
    @Autowired
    public SingleMessageQueue(final ApnsConfig apnsConfig) {
        super(apnsConfig.getMaxQueueSize());
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#put(java.lang.Object)
     */
    @Override
    public void put(final ApnsMessage apnsMessage) {

        // // TODO:Validate here.
        // final int currentCount = counter.incrementAndGet();
        // if (currentCount > SingleMessageQueue.MAX_SINGLE_QUEUE_SIZE) {
        // return false;
        // }
        try {
            super.put(apnsMessage);
        } catch (final InterruptedException e) {
            logger.error(SingleMessageQueue.INTERRUPTED_BY_SOME_ONE);
        }

    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#take()
     */
    @Override
    public ApnsMessage take() {
        ApnsMessage msg = null;
        try {
            msg = super.take();
        } catch (final InterruptedException e) {
            logger.error(SingleMessageQueue.INTERRUPTED_BY_SOME_ONE);
        }

        return msg;
    }

    /**
     * Push queue.
     * @param build
     *            the build
     */
    public void pushQueue(final ApnsMessage build) {
        put(build);

    }

    /**
     * Peek queue.
     * @return the apns message
     */
    public ApnsMessage peekQueue() {

        return take();
    }

}
