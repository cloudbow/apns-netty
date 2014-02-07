/*
 * FeedBackMessageInitalizer.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.handlers.feedback;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;
import apns.netty.decoders.apns.FeedBackDecoder;
import apns.netty.security.ApnsSSLContextFactory;
import apns.netty.security.ApnsSSLHandler;

/**
 * The Class FeedBackMessageInitalizer.
 * @author arung
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FeedBackMessageInitalizer extends
        ChannelInitializer<SocketChannel> {

    /** The Constant FEED_BACK_HANDLER_THREADS. */
    private static final int FEED_BACK_HANDLER_THREADS = 2;

    /** The Constant logger. */
    private static final Logger logger = Logger
            .getLogger(FeedBackMessageInitalizer.class);

    /** The apns config. */
    @Autowired
    ApnsConfig apnsConfig;

    /** The apns ssl context factory. */
    @Autowired
    private ApnsSSLContextFactory apnsSSLContextFactory;

    /** The application context. */
    @Autowired
    private ApplicationContext applicationContext;

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        FeedBackMessageInitalizer.logger.trace("getApnsSSLContextFactory()="
                                               + getApnsSSLContextFactory());
        FeedBackMessageInitalizer.logger
                .trace("getApnsSSLContextFactory().getSSLContext()="
                       + getApnsSSLContextFactory().getSSLContext());

        final SSLEngine engine = getApnsSSLContextFactory().getSSLContext()
                .createSSLEngine();
        FeedBackMessageInitalizer.logger.trace("SSLEngine is :" + engine);
        engine.setUseClientMode(true);

        final SslHandler sslHandler = new ApnsSSLHandler(engine);
        sslHandler.setHandshakeTimeout(apnsConfig.getHandShakeTimeout(),
                TimeUnit.MILLISECONDS);

        pipeline.addLast("ssl", sslHandler);

        // // On top of the SSL handler, add the text line codec.
        // pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
        // Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new FeedBackDecoder());

        // and then business logic.
        pipeline.addLast(new DefaultEventExecutorGroup(
                FeedBackMessageInitalizer.FEED_BACK_HANDLER_THREADS).next(),
                "handler", applicationContext.getBean(FeedBackHandler.class));
    }

    /**
     * Gets the apns ssl context factory.
     * @return the apns ssl context factory
     */
    public ApnsSSLContextFactory getApnsSSLContextFactory() {
        return apnsSSLContextFactory;
    }

    /**
     * Sets the apns ssl context factory.
     * @param apnsSSLContextFactory
     *            the new apns ssl context factory
     */
    public void setApnsSSLContextFactory(
            final ApnsSSLContextFactory apnsSSLContextFactory) {
        this.apnsSSLContextFactory = apnsSSLContextFactory;
    }

}