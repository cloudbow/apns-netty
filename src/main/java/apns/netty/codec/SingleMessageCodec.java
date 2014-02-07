/*
 * SingleMessageCodec.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.CharsetUtil;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.connpool.ConnectionPoolManagerFactory;
import apns.netty.exceptions.InvalidDeviceTokenFormatException;
import apns.netty.exceptions.MoreDataThanExpectedException;
import apns.netty.model.impl.ApnsMessage;
import apns.netty.model.impl.ApnsResponse;
import apns.netty.model.impl.ItemType;
import apns.netty.queues.single.SingleMessageQueue;

/**
 * @author arung
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SingleMessageCodec extends ByteToMessageCodec<ApnsMessage> {

    private static final Logger logger = Logger
            .getLogger(SingleMessageCodec.class);
    /** The Constant ALL_MESSAGES_WRITTEN_TO_CHANNEL_BY_ENCODER. */
    private static final String ALL_MESSAGES_WRITTEN_TO_CHANNEL_BY_ENCODER = "All messages written to channel by encoder:";

    /** The Constant CONSTRUCTED_FINAL_BUFFER. */
    private static final String CONSTRUCTED_FINAL_BUFFER = "Constructed final buffer:";

    /** The Constant COLON. */
    private static final String COLON = ":";

    /** The Constant FRAME_DATA_LENGTH. */
    private static final String FRAME_DATA_LENGTH = "frameDataLength=";

    /** The Constant PRIO_LENGTH. */
    private static final String PRIO_LENGTH = "PRIO  length:";

    /** The Constant EXP_DATE_LENGTH. */
    private static final String EXP_DATE_LENGTH = "EXP_DATE  length:";

    /** The Constant NOT_ID_LENGTH. */
    private static final String NOT_ID_LENGTH = "NOT_ID  length:";

    /** The Constant PAYLOAD_LENGTH. */
    private static final String PAYLOAD_LENGTH = "PAYLOAD  length:";

    /** The Constant DEV_TOKEN_LENGTH. */
    private static final String DEV_TOKEN_LENGTH = "DEV_TOKEN  length:";

    /** The Constant ITEMTYPE_IS. */
    private static final String ITEMTYPE_IS = "Itemtype is :";
    /** The Constant logger. */

    /** The Constant IDENTIFIER2. */
    private static final String IDENTIFIER2 = "identifier - ";

    /** The Constant STATUS2. */
    private static final String STATUS2 = " status-";

    /** The Constant COMMAND2. */
    private static final String COMMAND2 = "Command-";

    /** The Constant SINGLE_DECODE_PROTOCOL_SIZE. */
    private static final int SINGLE_DECODE_PROTOCOL_SIZE = 6;

    /** The single message queue. */
    @Autowired
    private SingleMessageQueue singleMessageQueue;

    /** The connection pool manager factory. */
    @Autowired
    private ConnectionPoolManagerFactory connectionPoolManagerFactory;

    /** The Constant COMMAND_BYTE. */
    private static final byte COMMAND_BYTE = 2;

    /** The apns message. */
    private ApnsMessage apnsMessage;

    /*
     * (non-Javadoc)
     * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.
     * ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
     */
    @Override
    protected void encode(final ChannelHandlerContext ctx,
            final ApnsMessage msg, final ByteBuf out) throws Exception {

        setApnsMessage(msg);

        validate(msg);
        int frameDataLength = 0;
        final ByteBufAllocator allocator2 = ctx.alloc();
        final CompositeByteBuf buf = allocator2.compositeDirectBuffer();

        buf.writeByte(SingleMessageCodec.COMMAND_BYTE);// #1

        final ByteBuf items = allocator2.directBuffer();
        for (final ItemType itType : ItemType.values()) {
            SingleMessageCodec.logger.trace(SingleMessageCodec.ITEMTYPE_IS
                                            + itType);
            final int itemSize = itType.getSize();
            final byte itemId = itType.getId();
            final Object obj = msg.getObject(itType);
            items.writeByte(itemId);
            frameDataLength += 3;
            switch (itType) {
            // DEV_TOKEN(1,32), PAYLOAD(2,256), NOT_ID(3,4), EXP_DATE(4,4),
            // PRIO(5,1);
            case DEV_TOKEN:
                final String deviceToken = (String) obj;
                final byte[] deviceTokenAsBytes = getDevTokenAsBytes(deviceToken);

                if (deviceTokenAsBytes.length != itemSize) {
                    throw new MoreDataThanExpectedException();
                }
                items.writeShort((short) itemSize);
                // items.writeBytes(SingleConnectionEncoder
                // .intTo2ByteArray(itemSize));
                items.writeBytes(deviceTokenAsBytes);
                SingleMessageCodec.logger
                        .trace(SingleMessageCodec.DEV_TOKEN_LENGTH
                               + deviceTokenAsBytes.length);
                frameDataLength += itemSize;

                break;
            case PAYLOAD:
                final byte[] payLoad = ((String) obj)
                        .getBytes(CharsetUtil.UTF_8);
                if (payLoad.length > itemSize) {
                    throw new MoreDataThanExpectedException();
                }

                items.writeShort((short) payLoad.length);
                items.writeBytes(payLoad);
                SingleMessageCodec.logger
                        .trace(SingleMessageCodec.PAYLOAD_LENGTH
                               + payLoad.length);
                frameDataLength += payLoad.length;

                break;
            case NOT_ID:

                items.writeShort((short) itemSize);
                items.writeInt((Integer) obj);
                SingleMessageCodec.logger
                        .trace(SingleMessageCodec.NOT_ID_LENGTH + itemSize);
                frameDataLength += itemSize;
                break;
            case EXP_DATE:

                items.writeShort((short) itemSize);
                items.writeInt((Integer) obj);
                SingleMessageCodec.logger
                        .trace(SingleMessageCodec.EXP_DATE_LENGTH + itemSize);
                frameDataLength += itemSize;
                break;
            case PRIO:

                items.writeShort((short) itemSize);
                items.writeByte((byte) obj);
                SingleMessageCodec.logger
                        .trace(SingleMessageCodec.PRIO_LENGTH + itemSize);
                frameDataLength += itemSize;

                break;

            }

        }
        SingleMessageCodec.logger.trace(SingleMessageCodec.FRAME_DATA_LENGTH
                                        + frameDataLength
                                        + SingleMessageCodec.COLON);

        buf.writeInt(frameDataLength);

        buf.writeBytes(items);

        SingleMessageCodec.logger
                .trace(SingleMessageCodec.CONSTRUCTED_FINAL_BUFFER
                       + buf.order() + SingleMessageCodec.COLON
                       + items.order() + SingleMessageCodec.COLON
                       + out.order());

        out.writeBytes(buf);

        SingleMessageCodec.logger
                .trace(SingleMessageCodec.ALL_MESSAGES_WRITTEN_TO_CHANNEL_BY_ENCODER);

        items.release();
        buf.release();

    }

    /*
     * (non-Javadoc)
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.
     * ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in,
            final List<Object> out) throws Exception {
        if (in.readableBytes() < SingleMessageCodec.SINGLE_DECODE_PROTOCOL_SIZE) {
            return;
        }

        final byte command = in.readByte();
        final byte status = in.readByte();
        final int identifier = in.readInt();
        final ApnsResponse response = new ApnsResponse();

        response.setCommand(command);
        response.setId(identifier);
        response.setStatus(status);
        out.add(response);
        SingleMessageCodec.logger.trace(SingleMessageCodec.COMMAND2
                                        + command
                                        + SingleMessageCodec.STATUS2
                                        + status
                                        + SingleMessageCodec.IDENTIFIER2
                                        + identifier);

        ctx.close();

        connectionPoolManagerFactory.getSingleConnection().bootstrap();

    }

    /**
     * Gets the dev token as bytes.
     * @param deviceToken
     *            the device token
     * @return the dev token as bytes
     * @throws InvalidDeviceTokenFormatException
     *             the invalid device token format exception
     */
    private byte[] getDevTokenAsBytes(String deviceToken)
            throws apns.netty.exceptions.InvalidDeviceTokenFormatException {
        final byte[] deviceTokenAsBytes = new byte[deviceToken.length() / 2];
        deviceToken = deviceToken.toUpperCase();
        int j = 0;
        try {
            for (int i = 0; i < deviceToken.length(); i += 2) {
                final String t = deviceToken.substring(i, i + 2);
                final int tmp = Integer.parseInt(t, 16);
                deviceTokenAsBytes[j++] = (byte) tmp;
            }
        } catch (final NumberFormatException e1) {
            throw new apns.netty.exceptions.InvalidDeviceTokenFormatException(
                    deviceToken, e1.getMessage());
        }
        return deviceTokenAsBytes;
    }

    /**
     * Validate.
     * @param msg
     *            the msg
     */
    private void validate(final ApnsMessage msg) {

    }

    /**
     * Gets the apns message.
     * @return the apns message
     */
    public ApnsMessage getApnsMessage() {
        return apnsMessage;
    }

    /**
     * Sets the apns message.
     * @param apnsMessage
     *            the new apns message
     */
    public void setApnsMessage(final ApnsMessage apnsMessage) {
        this.apnsMessage = apnsMessage;
    }
}
