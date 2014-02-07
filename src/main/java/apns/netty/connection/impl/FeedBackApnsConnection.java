/*
 * FeedBackApnsConnection.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.connection.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;
import apns.netty.connection.Connection;
import apns.netty.handlers.feedback.FeedBackHandler;
import apns.netty.handlers.feedback.FeedBackMessageInitalizer;

/**
 * The Class FeedBackConnection.
 * @author arung
 */
@Component
public class FeedBackApnsConnection implements Connection {

    /** The Constant APNS_FEEDBACK_CONNECTOR_THREAD. */
    private static final String APNS_FEEDBACK_CONNECTOR_THREAD = "APNSFeedbackConnectorThread";

    /** The Constant CLOSING_FEED_BACK_CHANNEL. */
    private static final String CLOSING_FEED_BACK_CHANNEL = "Closing feed back channel";

    /** The Constant FEEDBACK_CHANNEL_IS. */
    private static final String FEEDBACK_CHANNEL_IS = "Feedback channel is:";

    /** The logger. */
    Logger logger = Logger.getLogger(FeedBackApnsConnection.class);

    /** The apns config. */
    @Autowired
    ApnsConfig apnsConfig;

    /** The application context. */
    @Autowired
    ApplicationContext applicationContext;

    /** The channel. */
    private Channel channel;

    /** The feed back nio event loop group. */
    private final EventLoopGroup feedBackNioEventLoopGroup = new NioEventLoopGroup();

    /** The feed back boot strap. */
    @Autowired
    private Bootstrap feedBackBootStrap;

    /**
     * Inits the.
     */
    @PostConstruct
    public void init() {

        feedBackBootStrap.group(feedBackNioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                // .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(
                        applicationContext
                                .getBean(FeedBackMessageInitalizer.class));
        bootstrap();
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.connection.Connection#bootstrap()
     */
    @Scheduled(cron = "${APNSConfig.feedback.schedulerDelay}")
    @Override
    public void bootstrap() {

        scheduledConnect();

    }

    /**
     * Scheduled connect.
     */
    public void scheduledConnect() {
        if (channel != null) {
            channel.pipeline().context(FeedBackHandler.class).close(); // Close
                                                                       // the
                                                                       // channel
                                                                       // to
                                                                       // exit
                                                                       // the
                                                                       // thread
        }
        // // Make the connection attempt.
        // ChannelFuture f = b.connect(host, port).sync();
        //
        // // Wait until the connection is closed.
        // f.channel().closeFuture().sync();
        // } finally {
        // group.shutdownGracefully();
        // }
        // Start the connection attempt.

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final ChannelFuture f = feedBackBootStrap.connect(
                            apnsConfig.getFeedbackHost(),
                            apnsConfig.getFeedbackPort()).sync();

                    final Channel outChannel = f.channel();
                    logger.trace(FeedBackApnsConnection.FEEDBACK_CHANNEL_IS
                                 + outChannel);
                    channel = outChannel;
                    outChannel.closeFuture().sync();
                    logger.trace(FeedBackApnsConnection.CLOSING_FEED_BACK_CHANNEL);
                } catch (final InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    // TODO: Logger
                }
            }

        }, FeedBackApnsConnection.APNS_FEEDBACK_CONNECTOR_THREAD).start();

        // while (outChannel == null || !outChannel.isActive()) {
        // logger.trace("Not connected:");
        // }
    }

    /**
     * Destroy.
     */
    @PreDestroy
    public void destroy() {
        feedBackNioEventLoopGroup.shutdownGracefully();
    }

}
