/*
 * BatchMessageQueue.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.queues.batch;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;
import apns.netty.model.ApnsBatchMessage;

/**
 * The Class BatchMessageQueue.
 * @author arung
 */
@Component
public class BatchMessageQueue extends LinkedBlockingQueue<ApnsBatchMessage> {

    /** The Constant INTERRUPTED_BY_SOME_ONE. */
    private static final String INTERRUPTED_BY_SOME_ONE = "Interrupted by some one";

    /** The logger. */
    Logger logger = Logger.getLogger(BatchMessageQueue.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The apns config. */
    @Autowired
    ApnsConfig apnsConfig;

    /**
     * Instantiates a new batch message queue.
     * @param apnsConfig
     *            the apns config
     */
    @Autowired
    public BatchMessageQueue(final ApnsConfig apnsConfig) {
        super(apnsConfig.getMaxBatchQueueSize());
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#put(java.lang.Object)
     */
    @Override
    public void put(final ApnsBatchMessage apnsMessage) {

        // // TODO:Validate here.
        // final int currentCount = counter.incrementAndGet();
        // if (currentCount > SingleMessageQueue.MAX_SINGLE_QUEUE_SIZE) {
        // return false;
        // }
        try {
            super.put(apnsMessage);
        } catch (final InterruptedException e) {
            logger.error(BatchMessageQueue.INTERRUPTED_BY_SOME_ONE);
        }

    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#take()
     */
    @Override
    public ApnsBatchMessage take() {
        ApnsBatchMessage msg = null;
        try {
            msg = super.take();
        } catch (final InterruptedException e) {
            logger.error(BatchMessageQueue.INTERRUPTED_BY_SOME_ONE);
        }

        return msg;
    }

    /**
     * Push queue.
     * @param build
     *            the build
     */
    public void pushQueue(final ApnsBatchMessage build) {
        put(build);

    }

    /**
     * Peek queue.
     * @return the apns batch message
     */
    public ApnsBatchMessage peekQueue() {

        return take();
    }

}
