/*
 * ApnsSSLHandler.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.security;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;
import apns.netty.connpool.ConnectionPoolManagerFactory;

/**
 * The Class ApnsSSLHandler.
 * @author arung
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class ApnsSSLHandler extends SslHandler {

    /** The logger. */
    Logger logger = Logger.getLogger(ApnsSSLHandler.class);

    /** The apns config. */
    @Autowired
    private ApnsConfig apnsConfig;

    /** The connection pool manager factory. */
    @Autowired
    private ConnectionPoolManagerFactory connectionPoolManagerFactory;

    /**
     * Instantiates a new apns ssl handler.
     * @param engine
     *            the engine
     */
    public ApnsSSLHandler(final SSLEngine engine) {
        super(engine);

    }

    /*
     * (non-Javadoc)
     * @see io.netty.handler.ssl.SslHandler#exceptionCaught(io.netty.channel.
     * ChannelHandlerContext, java.lang.Throwable)
     */
    /**
     * Exception caught.
     * @param ctx
     *            the ctx
     * @param cause
     *            the cause
     * @throws Exception
     *             the exception
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) throws Exception {

        ctx.fireExceptionCaught(cause);

    }

}
