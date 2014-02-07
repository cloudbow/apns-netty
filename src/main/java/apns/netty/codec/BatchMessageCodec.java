/*
 * BatchMessageCodec.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.codec;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.exceptions.InvalidDeviceTokenFormatException;
import apns.netty.exceptions.MoreDataThanExpectedException;
import apns.netty.id.gen.BatchIdentifierGenerator;
import apns.netty.model.ApnsBatchMessage;
import apns.netty.model.impl.ApnsMessage;
import apns.netty.model.impl.ApnsResponse;
import apns.netty.model.impl.ItemType;
import apns.netty.queues.batch.BatchMessageQueue;

/**
 * The Class BatchMessageCodec.
 * @author arung
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BatchMessageCodec extends ByteToMessageCodec<ApnsBatchMessage> {

    /** The Constant IDENTIFIER2. */
    private static final String IDENTIFIER2 = "identifier - ";

    /** The Constant STATUS2. */
    private static final String STATUS2 = " status-";

    /** The Constant COMMAND2. */
    private static final String COMMAND2 = "Command-";

    /** The Constant ENCODING_ENDED. */
    private static final String ENCODING_ENDED = "=~ encoding ended =~";

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

    /** The Constant SENDING_NOTIFICATION_ID. */
    private static final String SENDING_NOTIFICATION_ID = "sending notification id:";

    /** The Constant PAYLOAD_LENGTH. */
    private static final String PAYLOAD_LENGTH = "PAYLOAD  length:";

    /** The Constant DEV_TOKEN_LENGTH. */
    private static final String DEV_TOKEN_LENGTH = "DEV_TOKEN  length:";

    /** The Constant DEVICE_TOKEN_IS. */
    private static final String DEVICE_TOKEN_IS = "Device token is:";

    /** The Constant ITEMTYPE_IS. */
    private static final String ITEMTYPE_IS = "Itemtype is :";

    /** The Constant ENCODING_STARTED. */
    private static final String ENCODING_STARTED = "~= encoding started ~=";

    /** The logger. */
    Logger logger = Logger.getLogger(BatchMessageCodec.class);

    /** The Constant COMMAND_BYTE. */
    private static final byte COMMAND_BYTE = 2;

    /** The apns batch message. */
    private ApnsBatchMessage apnsBatchMessage;

    /** The batch message queue. */
    @Autowired
    BatchMessageQueue batchMessageQueue;

    /** The batch identifier generator. */
    @Autowired
    private BatchIdentifierGenerator batchIdentifierGenerator;

    /** The Constant SINGLE_DECODE_PROTOCOL_SIZE. */
    private static final int SINGLE_DECODE_PROTOCOL_SIZE = 6;

    /*
     * (non-Javadoc)
     * @see io.netty.handler.codec.ByteToMessageCodec#encode(io.netty.channel.
     * ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
     */
    @Override
    protected void encode(final ChannelHandlerContext ctx,
            final ApnsBatchMessage batch, final ByteBuf out)
            throws Exception {
        logger.trace(BatchMessageCodec.ENCODING_STARTED);
        setApnsBatchMessage(batch);
        final ByteBufAllocator allocator2 = ctx.alloc();
        final CompositeByteBuf finalByteBuf = allocator2
                .compositeDirectBuffer();
        int i = 0;
        for (final ApnsMessage msg : batch.invariantMessages()) {
            i++;

            int frameDataLength = 0;

            finalByteBuf.writeByte(BatchMessageCodec.COMMAND_BYTE);// #1

            final ByteBuf items = allocator2.directBuffer();
            for (final ItemType itType : ItemType.values()) {
                logger.trace(BatchMessageCodec.ITEMTYPE_IS + itType);
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
                    logger.trace(BatchMessageCodec.DEVICE_TOKEN_IS
                                 + deviceToken);
                    final byte[] deviceTokenAsBytes = getDevTokenAsBytes(deviceToken);

                    if (deviceTokenAsBytes.length != itemSize) {
                        throw new MoreDataThanExpectedException();
                    }
                    items.writeShort((short) itemSize);
                    // items.writeBytes(SingleConnectionEncoder
                    // .intTo2ByteArray(itemSize));
                    items.writeBytes(deviceTokenAsBytes);
                    logger.trace(BatchMessageCodec.DEV_TOKEN_LENGTH
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
                    logger.trace(BatchMessageCodec.PAYLOAD_LENGTH
                                 + payLoad.length);
                    frameDataLength += payLoad.length;

                    break;
                case NOT_ID:

                    items.writeShort((short) itemSize);
                    // final Integer notId = (Integer) obj;
                    final Integer notId = batchIdentifierGenerator
                            .newIdentifier();
                    batch.updateBatchPosition(notId, i);
                    logger.trace(BatchMessageCodec.SENDING_NOTIFICATION_ID
                                 + notId);
                    items.writeInt(notId);
                    logger.trace(BatchMessageCodec.NOT_ID_LENGTH + itemSize);
                    frameDataLength += itemSize;
                    break;
                case EXP_DATE:

                    items.writeShort((short) itemSize);
                    items.writeInt((Integer) obj);
                    logger.trace(BatchMessageCodec.EXP_DATE_LENGTH
                                 + itemSize);
                    frameDataLength += itemSize;
                    break;
                case PRIO:

                    items.writeShort((short) itemSize);
                    items.writeByte((byte) obj);
                    logger.trace(BatchMessageCodec.PRIO_LENGTH + itemSize);
                    frameDataLength += itemSize;

                    break;

                }

            }
            logger.trace(BatchMessageCodec.FRAME_DATA_LENGTH
                         + frameDataLength + BatchMessageCodec.COLON);

            finalByteBuf.writeInt(frameDataLength);

            finalByteBuf.writeBytes(items);

        }
        logger.trace(BatchMessageCodec.ENCODING_ENDED);
        out.writeBytes(finalByteBuf);

    }

    /*
     * (non-Javadoc)
     * @see io.netty.handler.codec.ByteToMessageCodec#decode(io.netty.channel.
     * ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in,
            final List<Object> out) throws Exception {
        if (in.readableBytes() < BatchMessageCodec.SINGLE_DECODE_PROTOCOL_SIZE) {
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

        logger.trace(BatchMessageCodec.COMMAND2 + command
                     + BatchMessageCodec.STATUS2 + status
                     + BatchMessageCodec.IDENTIFIER2 + identifier);

    }

    /**
     * Gets the apns batch message.
     * @return the apns batch message
     */
    public ApnsBatchMessage getApnsBatchMessage() {
        return apnsBatchMessage;
    }

    /**
     * Sets the apns batch message.
     * @param apnsBatchMessage
     *            the new apns batch message
     */
    public void setApnsBatchMessage(final ApnsBatchMessage apnsBatchMessage) {
        this.apnsBatchMessage = apnsBatchMessage;
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
            throws InvalidDeviceTokenFormatException {
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
            throw new InvalidDeviceTokenFormatException(deviceToken,
                    e1.getMessage());
        }
        return deviceTokenAsBytes;
    }

}
