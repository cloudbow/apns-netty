/*
 * FeedBackDecoder.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.decoders.apns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import apns.netty.model.impl.FeedBackResponse;

/**
 * The Class FeedBackDecoder.
 * @author arung
 */
public class FeedBackDecoder extends ByteToMessageDecoder {

    /** The Constant SEC_CONV. */
    private static final int SEC_CONV = 1000;

    /** The Constant _0X000000FF. */
    private static final int _0X000000FF = 0x000000FF;

    /** The Constant DEV_TOKEN. */
    private static final String DEV_TOKEN = "+devToken+";

    /** The Constant TOKEN_LEN. */
    private static final String TOKEN_LEN = "+tokenLen+";

    /** The Constant TIME2. */
    private static final String TIME2 = "time-+";

    /** The Constant DECODING_FEEDBACK_RESPONSE. */
    private static final String DECODING_FEEDBACK_RESPONSE = "Decoding feedback response ";

    /** The Constant HEX_FORMAT. */
    private static final String HEX_FORMAT = "%02x";

    /** The Constant E_STRING. */
    private static final String E_STRING = "";

    /** The logger. */
    Logger logger = Logger.getLogger(FeedBackDecoder.class);

    /** The Constant FEEDBACK_PROTOCOL_DECODE_SIZE. */
    private static final int FEEDBACK_PROTOCOL_DECODE_SIZE = 38;

    /*
     * (non-Javadoc)
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.
     * ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    /**
     * Decode.
     * @param ctx
     *            the ctx
     * @param in
     *            the in
     * @param out
     *            the out
     * @throws Exception
     *             the exception
     */
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in,
            final List<Object> out) throws Exception {
        logger.trace(FeedBackDecoder.DECODING_FEEDBACK_RESPONSE);
        if (in.readableBytes() < FeedBackDecoder.FEEDBACK_PROTOCOL_DECODE_SIZE) {
            return;
        }

        final int time = in.readInt();
        final int tokenLen = in.readShort();
        final byte[] devToken = in.readBytes(tokenLen).array();
        logger.trace(FeedBackDecoder.TIME2 + time
                     + FeedBackDecoder.TOKEN_LEN + tokenLen
                     + FeedBackDecoder.DEV_TOKEN + devToken);
        int octet = 0;
        String deviceToken = FeedBackDecoder.E_STRING;
        for (final byte b : devToken) {
            octet = FeedBackDecoder._0X000000FF & b;
            deviceToken = deviceToken.concat(String.format(
                    FeedBackDecoder.HEX_FORMAT, octet));
        }

        final Timestamp timestamp = new Timestamp(time
                                                  * FeedBackDecoder.SEC_CONV);

        out.add(new FeedBackResponse(timestamp, deviceToken));
    }

}
