/*
 * SingleMessageHandler.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.handlers.single;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.codec.SingleMessageCodec;
import apns.netty.connpool.ConnectionPoolManagerFactory;
import apns.netty.queues.single.SingleMessageQueue;

/**
 * The Class SingleMessageHandler.
 * @author arung
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class SingleMessageHandler extends ChannelHandlerAdapter {

    /** The Constant CLOSING_THE_CONTEXT_AND_RETRYING. */
    private static final String CLOSING_THE_CONTEXT_AND_RETRYING = "Closing the context and retrying...";

    /** The Constant UNEXPECTED_EXCEPTION_FROM_DOWNSTREAM. */
    private static final String UNEXPECTED_EXCEPTION_FROM_DOWNSTREAM = "Unexpected exception from downstream.";

    /** The Constant CHANNEL_INACTIVE_DOWNSTREAM. */
    private static final String CHANNEL_INACTIVE_DOWNSTREAM = "channelInactive downstream.";

    /** The Constant logger. */
    private static final Logger logger = Logger
            .getLogger(SingleMessageHandler.class.getName());

    /** The single message queue. */
    @Autowired
    private SingleMessageQueue singleMessageQueue;

    /** The connection pool manager factory. */
    @Autowired
    private ConnectionPoolManagerFactory connectionPoolManagerFactory;

    /*
     * (non-Javadoc)
     * @see
     * io.netty.channel.ChannelHandlerAdapter#channelInactive(io.netty.channel
     * .ChannelHandlerContext)
     */
    /**
     * Channel inactive.
     * @param ctx
     *            the ctx
     * @throws Exception
     *             the exception
     */
    @Override
    @Skip
    public void channelInactive(final ChannelHandlerContext ctx)
            throws Exception {
        SingleMessageHandler.logger
                .trace(SingleMessageHandler.CHANNEL_INACTIVE_DOWNSTREAM);
        super.channelInactive(ctx);
    }

    /*
     * (non-Javadoc)
     * @see
     * io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel
     * .ChannelHandlerContext, java.lang.Throwable)
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
        SingleMessageHandler.logger
                .trace(SingleMessageHandler.UNEXPECTED_EXCEPTION_FROM_DOWNSTREAM
                       + cause);
        SingleMessageHandler.logger
                .trace(SingleMessageHandler.CLOSING_THE_CONTEXT_AND_RETRYING);
        if (cause instanceof IOException) {
            if (ctx.channel().isActive()) {
                ctx.close();
            }

            singleMessageQueue.pushQueue(ctx.pipeline()
                    .get(SingleMessageCodec.class).getApnsMessage());

            connectionPoolManagerFactory.getSingleConnection().bootstrap();
        } else {
            super.exceptionCaught(ctx, cause);
        }

    }
}
