/*
 * SingleApnsConnection.java
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
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;
import apns.netty.connection.Connection;
import apns.netty.constants.ApplicationContextComponents;
import apns.netty.handlers.single.SingleMessageInitializer;
import apns.netty.queues.single.SingleMessageQueue;
import apns.netty.queues.single.SingleMessageQueuePoller;

/**
 * The Class SingleConnection.
 * @author arung
 */
@Component
public class SingleApnsConnection implements Connection {

    /** The Constant CLOSING_SINGLE_CONNECTION_CHANNEL. */
    private static final String CLOSING_SINGLE_CONNECTION_CHANNEL = "Closing SingleConnection channel";

    /** The Constant SINGLE_CONNECTION_CHANNEL_IS. */
    private static final String SINGLE_CONNECTION_CHANNEL_IS = "SingleConnection channel is:";

    /** The logger. */
    Logger logger = Logger.getLogger(SingleApnsConnection.class);

    /** The apns config. */
    @Autowired
    ApnsConfig apnsConfig;

    /** The application context. */
    @Autowired
    ApplicationContext applicationContext;

    /** The single message queue. */
    @Autowired
    SingleMessageQueue singleMessageQueue;

    /** The single bootstrap. */
    @Autowired
    private Bootstrap singleBootstrap;

    /** The single nio event loop group. */
    private final EventLoopGroup singleNioEventLoopGroup = new NioEventLoopGroup();

    /**
     * Inits the.
     */
    @PostConstruct
    public void init() {

        singleBootstrap = new Bootstrap();
        singleBootstrap.group(singleNioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                // .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(
                        applicationContext
                                .getBean(SingleMessageInitializer.class));
        bootstrap();
    }

    /*
     * (non-Javadoc)
     * @see apns.netty.connection.Connection#connect()
     */
    @Override
    public void bootstrap() {

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
                    final ChannelFuture f = singleBootstrap.connect(
                            apnsConfig.getPushHost(),
                            apnsConfig.getPushPort()).sync();

                    final Channel outChannel = f.channel();
                    logger.trace(SingleApnsConnection.SINGLE_CONNECTION_CHANNEL_IS
                                 + outChannel);
                    final SingleMessageQueuePoller singleMessageQueuePoller = (SingleMessageQueuePoller) applicationContext.getBean(ApplicationContextComponents.SINGLE_MESSAGE_QUEUE_POLLER);
                    singleMessageQueuePoller.setOutChannel(outChannel);
                    if (!singleMessageQueuePoller.isAlive()) {
                        singleMessageQueuePoller.start();
                    }

                    outChannel.closeFuture().sync();
                    logger.trace(SingleApnsConnection.CLOSING_SINGLE_CONNECTION_CHANNEL);
                } catch (final InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    // TODO: Logger
                }
            }

        }, "APNSSingleConnectorThread").start();

        // while (outChannel == null || !outChannel.isActive()) {
        // logger.trace("Not connected:");
        // }

    }

    /**
     * Destroy.
     */
    @PreDestroy
    public void destroy() {
        singleNioEventLoopGroup.shutdownGracefully();
    }

    // public void write(final ApnsMessage msg) {
    // logger.trace("Writing message to single connection");
    // outChannel.write(msg);
    // logger.trace("Flusing connetion");
    // outChannel.flush();
    // logger.trace("Flusing the data");
    // }

}
