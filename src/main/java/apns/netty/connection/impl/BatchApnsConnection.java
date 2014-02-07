/*
 * BatchApnsConnection.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.connection.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;
import apns.netty.connection.Connection;
import apns.netty.handlers.batch.BatchMessageInitializer;
import apns.netty.model.ApnsBatchMessage;
import apns.netty.queues.batch.BatchMessageQueue;

/**
 * The Class BatchConnection.
 * @author arung
 */
@Component
public class BatchApnsConnection implements Connection {

    /** The Constant INTERRUPTED_EXCEPTION. */
    private static final String INTERRUPTED_EXCEPTION = "Interrupted exception";

    /** The Constant RETRY_COUNT. */
    private static final int RETRY_COUNT = 5;

    /** The Constant WRITING_USING_RANDOM_CHANNEL. */
    private static final String WRITING_USING_RANDOM_CHANNEL = "Writing using random channel:";

    /** The Constant APNS_BATCH_CONNECTOR_THREAD. */
    private static final String APNS_BATCH_CONNECTOR_THREAD = "APNSBatchConnectorThread-";

    /**
     * The Class ChannelCreator.
     * @author arung
     */
    private final class ChannelCreator implements Runnable {

        /** The Constant RETRYING_BATCH_CONNECTION. */
        private static final String RETRYING_BATCH_CONNECTION = "RETRYING BATCH CONNECTION...";

        /** The Constant INTERRUPTED. */
        private static final String INTERRUPTED = "Interrupted";

        /** The Constant CLOSING_BATCH_CONNECTION_CHANNEL. */
        private static final String CLOSING_BATCH_CONNECTION_CHANNEL = "CLOSING BATCH CONNECTION CHANNEL...";

        /** The Constant COLON. */
        private static final String COLON = " : ";

        /** The Constant BATCH_CONNECTION_CHANNEL_IS. */
        private static final String BATCH_CONNECTION_CHANNEL_IS = "BatchConnection channel  is ";

        /** The batch connection. */
        BatchApnsConnection batchConnection;

        /**
         * Instantiates a new channel creator.
         * @param b3
         *            the b3
         */
        ChannelCreator(final BatchApnsConnection b3) {
            batchConnection = b3;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            Channel outChannel = null;
            try {

                final ChannelFuture f = batchBootstrap.connect(
                        apnsConfig.getPushHost(), apnsConfig.getPushPort())
                        .sync();

                outChannel = f.channel();
                logger.trace(ChannelCreator.BATCH_CONNECTION_CHANNEL_IS
                             + ChannelCreator.COLON + outChannel);
                getAtomicBatchCounter().incrementAndGet();
                getBatchChannelGroup().add(outChannel);
                outChannel.closeFuture().sync();

                logger.trace(ChannelCreator.CLOSING_BATCH_CONNECTION_CHANNEL);

            } catch (final InterruptedException e) {

                logger.trace(ChannelCreator.INTERRUPTED);
            } finally {
                logger.trace(ChannelCreator.RETRYING_BATCH_CONNECTION);
                getAtomicBatchCounter().decrementAndGet();
                getBatchChannelGroup().remove(outChannel);
                batchConnection.createAndSync(1);
            }
        }
    }

    /** The logger. */

    Logger logger = Logger.getLogger(BatchApnsConnection.class);

    /** The apns config. */
    @Autowired
    ApnsConfig apnsConfig;

    /** The application context. */
    @Autowired
    ApplicationContext applicationContext;

    /** The batch bootstrap. */
    @Autowired
    private Bootstrap batchBootstrap;

    /** The batch message queue. */
    @Autowired
    private BatchMessageQueue batchMessageQueue;

    /** The batch channel group. */
    private BlockingQueue<Channel> batchChannelGroup;

    /** The atomic batch counter. */
    private final AtomicInteger atomicBatchCounter = new AtomicInteger(0);

    /** The atomic thread counter. */
    private final AtomicInteger atomicThreadCounter = new AtomicInteger(0);

    /** The batch nio event loop group. */
    private final EventLoopGroup batchNioEventLoopGroup;

    /**
     * Instantiates a new batch connection.
     * @param apnsConfig
     *            the apns config
     */
    @Autowired
    public BatchApnsConnection(final ApnsConfig apnsConfig) {
        batchNioEventLoopGroup = new NioEventLoopGroup(
                apnsConfig.getBatchProcessingCount());

        setBatchChannelGroup(new LinkedBlockingQueue<Channel>());

    }

    /**
     * Inits the.
     */
    @PostConstruct
    public void init() {
        batchBootstrap = new Bootstrap();
        batchBootstrap.group(batchNioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                // .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(
                        applicationContext
                                .getBean(BatchMessageInitializer.class));
        bootstrap();
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.connection.Connection#connect()
     */
    @Override
    public void bootstrap() {

        startBatch(apnsConfig.getBatchAcceptConnCount()
                   + apnsConfig.getBatchProcssConnCount());

    }

    /**
     * Start batch.
     * @param batchSize
     *            the batch size
     */
    public void startBatch(final int batchSize) {

        synchronized (getBatchChannelGroup()) {
            for (int i = 0; i < batchSize; i++) {

                createAndSync(i);
            }

        }

    }

    /**
     * Creates the and sync.
     * @param i
     *            the i
     */
    private void createAndSync(final int i) {

        new Thread(new ChannelCreator(this),
                BatchApnsConnection.APNS_BATCH_CONNECTOR_THREAD
                        + atomicThreadCounter.incrementAndGet()).start();
    }

    /**
     * Random write.
     * @param msg
     *            the msg
     */
    public void randomWrite(final ApnsBatchMessage msg) {
        // REFACTOR WAIT
        Channel ch = null;
        int count = 0;
        while (count < BatchApnsConnection.RETRY_COUNT) {
            count++;
            try {
                ch = getBatchChannelGroup().take();

            } catch (final InterruptedException e) {
                logger.trace(BatchApnsConnection.INTERRUPTED_EXCEPTION);

            }
            if (ch.isActive()) {

                logger.trace(BatchApnsConnection.WRITING_USING_RANDOM_CHANNEL
                             + ch);
                getBatchChannelGroup().add(ch);
                ch.writeAndFlush(msg);

                break;
            } else {
                // removed by creator thread

            }
        }

        if (count == BatchApnsConnection.RETRY_COUNT) {
            logger.trace("Found no channels even after 10 retries..giving up");
            // Add exception;
        }

        // logger.trace(BatchApnsConnection.STARTING_SHUFFLING);
        // Collections.shuffle(channels);
        // logger.trace(BatchApnsConnection.ENDING_SHUFFLING);
        // final int intValue = atomicBatchCounter.intValue();
        // logger.trace("Total channels is:" + intValue);
        // final int index = new Random().nextInt(intValue);
        // logger.trace("Index of channel set is:" + index);
        // Channel channel;
        // try {
        // channel = getBatchChannelGroup().take();
        // } catch (final InterruptedException e) {
        // logger.error("Interrupted...");
        // return;
        // }
        //
        // if (channel.isActive()) {
        // logger.trace("Got Random channel:" + channel
        // + ":isChannelActive=" + channel.isActive());
        // getBatchChannelGroup().add(channel);
        // channel.writeAndFlush(msg);
        //
        // } else {
        //
        // logger.trace("Channel removed from list since its not active");
        // }

    }

    /**
     * Gets the atomic batch counter.
     * @return the atomic batch counter
     */
    public AtomicInteger getAtomicBatchCounter() {
        return atomicBatchCounter;
    }

    /**
     * Sets the batch channel group.
     * @param batchChannelGroup
     *            the new batch channel group
     */
    public void setBatchChannelGroup(
            final BlockingQueue<Channel> batchChannelGroup) {
        this.batchChannelGroup = batchChannelGroup;
    }

    /**
     * Gets the batch channel group.
     * @return the batch channel group
     */
    public BlockingQueue<Channel> getBatchChannelGroup() {
        return this.batchChannelGroup;
    }

}
