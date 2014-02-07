/*
 * FeedBackHandler.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.handlers.feedback;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.ext.actions.DecoderAction;
import apns.netty.handlers.single.SingleMessageHandler;
import apns.netty.model.impl.FeedBackResponse;

/**
 * The Class FeedBackHandler.
 * @author arung
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class FeedBackHandler extends
        SimpleChannelInboundHandler<List<FeedBackResponse>> {
    
    /** The Constant logger. */
    private static final Logger logger = Logger
            .getLogger(SingleMessageHandler.class.getName());

    /** The application context. */
    private ApplicationContext applicationContext;

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
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
        FeedBackHandler.logger.trace("Unexpected exception from downstream."
                                     + cause);

    }

    /*
     * (non-Javadoc)
     * @see
     * io.netty.channel.SimpleChannelInboundHandler#messageReceived(io.netty
     * .channel.ChannelHandlerContext, java.lang.Object)
     */
    /**
     * Message received.
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @throws Exception
     *             the exception
     */
    @Override
    protected void messageReceived(final ChannelHandlerContext ctx,
            final List<FeedBackResponse> msg) throws Exception {
        FeedBackHandler.logger.trace("REceieved feedback message:" + msg);
        final DecoderAction action = (DecoderAction) applicationContext
                .getBean("decoderProvider"); // Must be defined in app context;
        if (action != null) {
            action.afterDecode(msg);
        }

    }
}
