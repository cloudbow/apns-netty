/*
 * BatchApnsMessagesHandler.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.handlers.batch;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.codec.BatchMessageCodec;
import apns.netty.connpool.ConnectionPoolManagerFactory;
import apns.netty.model.impl.ApnsResponse;
import apns.netty.model.impl.BatchFullMessage;
import apns.netty.queues.batch.BatchMessageQueue;

/**
 * The Class BatchApnsMessagesHandler.
 * @author arung
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class BatchApnsMessagesHandler extends
        SimpleChannelInboundHandler<ApnsResponse> {

    /** The Constant CLOSING_THE_CONTEXT_AND_RETRYING. */
    private static final String CLOSING_THE_CONTEXT_AND_RETRYING = "Closing the context and retrying...";

    /** The Constant UNEXPECTED_EXCEPTION_FROM_DOWNSTREAM. */
    private static final String UNEXPECTED_EXCEPTION_FROM_DOWNSTREAM = "Unexpected exception from downstream.";

    /** The Constant logger. */
    private static final Logger logger = Logger
            .getLogger(BatchApnsMessagesHandler.class.getName());

    /** The batch message queue. */
    @Autowired
    BatchMessageQueue batchMessageQueue;

    /** The connection pool manager factory. */
    @Autowired
    ConnectionPoolManagerFactory connectionPoolManagerFactory;

    /*
     * (non-Javadoc)
     * @see
     * io.netty.channel.SimpleChannelInboundHandler#messageReceived(io.netty
     * .channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void messageReceived(final ChannelHandlerContext ctx,
            final ApnsResponse apnsResponse) throws Exception {
        final BatchFullMessage apnsBatchMessage = (BatchFullMessage) ctx
                .pipeline().get(BatchMessageCodec.class)
                .getApnsBatchMessage();

        final BatchFullMessage msg = apnsBatchMessage;
        msg.sliceAndStoreMessages(apnsResponse.getId());

        ctx.close();

        batchMessageQueue.pushQueue(msg);

    }

    /*
     * (non-Javadoc)
     * @see
     * io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel
     * .ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) throws Exception {

        BatchApnsMessagesHandler.logger
                .trace(BatchApnsMessagesHandler.UNEXPECTED_EXCEPTION_FROM_DOWNSTREAM
                       + cause);
        BatchApnsMessagesHandler.logger
                .trace(BatchApnsMessagesHandler.CLOSING_THE_CONTEXT_AND_RETRYING);
        if (cause instanceof IOException) {
            if (ctx.channel().isActive()) {
                ctx.close();
            }

            batchMessageQueue.pushQueue(ctx.pipeline()
                    .get(BatchMessageCodec.class).getApnsBatchMessage());

        } else {
            super.exceptionCaught(ctx, cause);
        }

    }

}
