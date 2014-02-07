/*
 * SingleMessageInitializer.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.handlers.single;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.codec.SingleMessageCodec;
import apns.netty.config.ApnsConfig;
import apns.netty.security.ApnsSSLContextFactory;
import apns.netty.security.ApnsSSLHandler;

/**
 * Handles a client-side channel.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SingleMessageInitializer extends
        ChannelInitializer<SocketChannel> {

    /** The Constant HANDLER. */
    private static final String HANDLER = "handler";

    /** The Constant SSL. */
    private static final String SSL = "ssl";

    /** The Constant SSL_ENGINE_IS. */
    private static final String SSL_ENGINE_IS = "SSLEngine is :";

    /** The Constant GET_APNS_SSL_CONTEXT_FACTORY_GET_SSL_CONTEXT. */
    private static final String GET_APNS_SSL_CONTEXT_FACTORY_GET_SSL_CONTEXT = "getApnsSSLContextFactory().getSSLContext()=";

    /** The Constant GET_APNS_SSL_CONTEXT_FACTORY. */
    private static final String GET_APNS_SSL_CONTEXT_FACTORY = "getApnsSSLContextFactory()=";

    /** The Constant logger. */
    private static final Logger logger = Logger
            .getLogger(SingleMessageInitializer.class);

    /** The Constant CODEC. */
    private static final String CODEC = "codec";

    /** The apns config. */
    @Autowired
    ApnsConfig apnsConfig;

    /** The apns ssl context factory. */
    @Autowired
    private ApnsSSLContextFactory apnsSSLContextFactory;

    /** The application context. */
    @Autowired
    private ApplicationContext applicationContext;

    /*
     * (non-Javadoc)
     * @see
     * io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    /**
     * Inits the channel.
     * @param ch
     *            the ch
     * @throws Exception
     *             the exception
     */
    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        SingleMessageInitializer.logger
                .trace(SingleMessageInitializer.GET_APNS_SSL_CONTEXT_FACTORY
                       + getApnsSSLContextFactory());
        SingleMessageInitializer.logger
                .trace(SingleMessageInitializer.GET_APNS_SSL_CONTEXT_FACTORY_GET_SSL_CONTEXT
                       + getApnsSSLContextFactory().getSSLContext());

        final SSLEngine engine = getApnsSSLContextFactory().getSSLContext()
                .createSSLEngine();
        SingleMessageInitializer.logger
                .trace(SingleMessageInitializer.SSL_ENGINE_IS + engine);
        engine.setUseClientMode(true);

        final SslHandler sslHandler = new ApnsSSLHandler(engine);
        sslHandler.setHandshakeTimeout(apnsConfig.getHandShakeTimeout(),
                TimeUnit.MILLISECONDS);

        pipeline.addLast(SingleMessageInitializer.SSL, sslHandler);

        // // On top of the SSL handler, add the text line codec.
        // pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
        // Delimiters.lineDelimiter()));
        pipeline.addLast(SingleMessageInitializer.CODEC,
                applicationContext.getBean(SingleMessageCodec.class));

        // and then business logic.
        pipeline.addLast(SingleMessageInitializer.HANDLER,
                applicationContext.getBean(SingleMessageHandler.class));
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
