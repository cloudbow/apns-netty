/*
 * SingleMessageQueuePoller.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.queues.single;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apns.netty.model.impl.ApnsMessage;

/**
 * The Class SingleMessageQueuePoller.
 */
@Component
public class SingleMessageQueuePoller extends Thread {

    /** The Constant FRIRING_UP_SINGLE_QUEUE_POLLER. */
    private static final String FRIRING_UP_SINGLE_QUEUE_POLLER = "Friring up single queue poller";

    /** The Constant APNS_SINGLE_MESSAGE_QUEUE_POLLER. */
    private static final String APNS_SINGLE_MESSAGE_QUEUE_POLLER = "APNSSingleMessageQueuePoller";

    /** The Constant logger. */
    private static final Logger logger = Logger
            .getLogger(SingleMessageQueuePoller.class);

    /** The out channel. */
    private volatile Channel outChannel;

    /** The single message queue. */
    @Autowired
    private SingleMessageQueue singleMessageQueue;

    /**
     * Instantiates a new single message queue poller.
     */
    SingleMessageQueuePoller() {
        super(SingleMessageQueuePoller.APNS_SINGLE_MESSAGE_QUEUE_POLLER);

    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        SingleMessageQueuePoller.logger
                .trace(SingleMessageQueuePoller.FRIRING_UP_SINGLE_QUEUE_POLLER);
        while (true) {

            if (getOutChannel() != null && getOutChannel().isActive()) {

                final ApnsMessage message = singleMessageQueue.peekQueue();

                if (message != null) {
                    getOutChannel().pipeline().writeAndFlush(message)
                            .addListener(new ChannelFutureListener() {

                                @Override
                                public void operationComplete(
                                        final ChannelFuture future)
                                        throws Exception {
                                    if (!future.isSuccess()) {
                                        singleMessageQueue
                                                .pushQueue(message);
                                    }

                                }

                            });
                }
            }
        }

    }

    /**
     * Gets the out channel.
     * @return the out channel
     */
    public Channel getOutChannel() {
        return outChannel;
    }

    /**
     * Sets the out channel.
     * @param outChannel
     *            the new out channel
     */
    public void setOutChannel(final Channel outChannel) {
        this.outChannel = outChannel;
    }

}