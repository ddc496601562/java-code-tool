/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;

import java.nio.ByteOrder;
import java.util.Set;

import static org.jboss.netty.handler.codec.spdy.SpdyCodecUtil.*;

/**
 * Encodes a SPDY Frame into a {@link ChannelBuffer}.
 */
public class SpdyFrameEncoder implements ChannelDownstreamHandler {

    private final int version;
    private final SpdyHeaderBlockEncoder headerBlockEncoder;

    /**
     * Creates a new instance with the specified {@code version} and the
     * default {@code compressionLevel (6)}, {@code windowBits (15)},
     * and {@code memLevel (8)}.
     */
    public SpdyFrameEncoder(int version) {
        this(version, 6, 15, 8);
    }

    /**
     * Creates a new instance with the specified parameters.
     */
    public SpdyFrameEncoder(int version, int compressionLevel, int windowBits, int memLevel) {
        this(version, SpdyHeaderBlockEncoder.newInstance(
                version, compressionLevel, windowBits, memLevel));
    }

    protected SpdyFrameEncoder(int version, SpdyHeaderBlockEncoder headerBlockEncoder) {
        if (version < SPDY_MIN_VERSION || version > SPDY_MAX_VERSION) {
            throw new IllegalArgumentException(
                    "unknown version: " + version);
        }
        this.version = version;
        this.headerBlockEncoder = headerBlockEncoder;
    }

    public void handleDownstream(
            final ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        if (evt instanceof ChannelStateEvent) {
            ChannelStateEvent e = (ChannelStateEvent) evt;
            switch (e.getState()) {
            case OPEN:
            case CONNECTED:
            case BOUND:
                if (Boolean.FALSE.equals(e.getValue()) || e.getValue() == null) {
                    synchronized (headerBlockEncoder) {
                        headerBlockEncoder.end();
                    }
                }
            }
        }

        if (!(evt instanceof MessageEvent)) {
            ctx.sendDownstream(evt);
            return;
        }

        final MessageEvent e = (MessageEvent) evt;
        Object msg = e.getMessage();

        if (msg instanceof SpdyDataFrame) {

            SpdyDataFrame spdyDataFrame = (SpdyDataFrame) msg;
            ChannelBuffer data = spdyDataFrame.getData();
            byte flags = spdyDataFrame.isLast() ? SPDY_DATA_FLAG_FIN : 0;
            ChannelBuffer header = ChannelBuffers.buffer(
                    ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE);
            header.writeInt(spdyDataFrame.getStreamId() & 0x7FFFFFFF);
            header.writeByte(flags);
            header.writeMedium(data.readableBytes());
            ChannelBuffer frame = ChannelBuffers.wrappedBuffer(header, data);
            Channels.write(ctx, e.getFuture(), frame, e.getRemoteAddress());
            return;
        }

        if (msg instanceof SpdySynStreamFrame) {

            synchronized (headerBlockEncoder) {
                SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame) msg;
                ChannelBuffer data = headerBlockEncoder.encode(spdySynStreamFrame);
                byte flags = spdySynStreamFrame.isLast() ? SPDY_FLAG_FIN : 0;
                if (spdySynStreamFrame.isUnidirectional()) {
                    flags |= SPDY_FLAG_UNIDIRECTIONAL;
                }
                int headerBlockLength = data.readableBytes();
                int length;
                if (version < 3) {
                    length = headerBlockLength == 0 ? 12 : 10 + headerBlockLength;
                } else {
                    length = 10 + headerBlockLength;
                }
                ChannelBuffer frame = ChannelBuffers.buffer(
                        ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE + 12);
                frame.writeShort(version | 0x8000);
                frame.writeShort(SPDY_SYN_STREAM_FRAME);
                frame.writeByte(flags);
                frame.writeMedium(length);
                frame.writeInt(spdySynStreamFrame.getStreamId());
                frame.writeInt(spdySynStreamFrame.getAssociatedToStreamId());
                if (version < 3) {
                    // Restrict priorities for SPDY/2 to between 0 and 3
                    byte priority = spdySynStreamFrame.getPriority();
                    if (priority > 3) {
                        priority = 3;
                    }
                    frame.writeShort((priority & 0xFF) << 14);
                } else {
                    frame.writeShort((spdySynStreamFrame.getPriority() & 0xFF) << 13);
                }
                if (version < 3 && data.readableBytes() == 0) {
                    frame.writeShort(0);
                }
                // Writes of compressed data must occur in order
                final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(frame, data);
                Channels.write(ctx, e.getFuture(), buffer, e.getRemoteAddress());
            }
            return;
        }

        if (msg instanceof SpdySynReplyFrame) {

            synchronized (headerBlockEncoder) {
                SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame) msg;
                ChannelBuffer data = headerBlockEncoder.encode(spdySynReplyFrame);
                byte flags = spdySynReplyFrame.isLast() ? SPDY_FLAG_FIN : 0;
                int headerBlockLength = data.readableBytes();
                int length;
                if (version < 3) {
                    length = headerBlockLength == 0 ? 8 : 6 + headerBlockLength;
                } else {
                    length = 4 + headerBlockLength;
                }
                ChannelBuffer frame = ChannelBuffers.buffer(
                        ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE + 8);
                frame.writeShort(version | 0x8000);
                frame.writeShort(SPDY_SYN_REPLY_FRAME);
                frame.writeByte(flags);
                frame.writeMedium(length);
                frame.writeInt(spdySynReplyFrame.getStreamId());
                if (version < 3) {
                    if (data.readableBytes() == 0) {
                        frame.writeInt(0);
                    } else {
                        frame.writeShort(0);
                    }
                }
                // Writes of compressed data must occur in order
                final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(frame, data);
                Channels.write(ctx, e.getFuture(), buffer, e.getRemoteAddress());
            }
            return;
        }

        if (msg instanceof SpdyRstStreamFrame) {

            SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame) msg;
            ChannelBuffer frame = ChannelBuffers.buffer(
                    ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE + 8);
            frame.writeShort(version | 0x8000);
            frame.writeShort(SPDY_RST_STREAM_FRAME);
            frame.writeInt(8);
            frame.writeInt(spdyRstStreamFrame.getStreamId());
            frame.writeInt(spdyRstStreamFrame.getStatus().getCode());
            Channels.write(ctx, e.getFuture(), frame, e.getRemoteAddress());
            return;
        }

        if (msg instanceof SpdySettingsFrame) {

            SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame) msg;
            byte flags = spdySettingsFrame.clearPreviouslyPersistedSettings() ?
                SPDY_SETTINGS_CLEAR : 0;
            Set<Integer> IDs = spdySettingsFrame.getIds();
            int numEntries = IDs.size();
            int length = 4 + numEntries * 8;
            ChannelBuffer frame = ChannelBuffers.buffer(
                    ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE + length);
            frame.writeShort(version | 0x8000);
            frame.writeShort(SPDY_SETTINGS_FRAME);
            frame.writeByte(flags);
            frame.writeMedium(length);
            frame.writeInt(numEntries);
            for (Integer ID: IDs) {
                int id = ID.intValue();
                byte ID_flags = 0;
                if (spdySettingsFrame.isPersistValue(id)) {
                    ID_flags |= SPDY_SETTINGS_PERSIST_VALUE;
                }
                if (spdySettingsFrame.isPersisted(id)) {
                    ID_flags |= SPDY_SETTINGS_PERSISTED;
                }
                if (version < 3) {
                    // Chromium Issue 79156
                    // SPDY setting ids are not written in network byte order
                    // Write id assuming the architecture is little endian
                    frame.writeByte(id & 0xFF);
                    frame.writeByte(id >>  8 & 0xFF);
                    frame.writeByte(id >> 16 & 0xFF);
                    frame.writeByte(ID_flags);
                } else {
                    frame.writeByte(ID_flags);
                    frame.writeMedium(id);
                }
                frame.writeInt(spdySettingsFrame.getValue(id));
            }
            Channels.write(ctx, e.getFuture(), frame, e.getRemoteAddress());
            return;
        }

        if (msg instanceof SpdyPingFrame) {

            SpdyPingFrame spdyPingFrame = (SpdyPingFrame) msg;
            ChannelBuffer frame = ChannelBuffers.buffer(
                    ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE + 4);
            frame.writeShort(version | 0x8000);
            frame.writeShort(SPDY_PING_FRAME);
            frame.writeInt(4);
            frame.writeInt(spdyPingFrame.getId());
            Channels.write(ctx, e.getFuture(), frame, e.getRemoteAddress());
            return;
        }

        if (msg instanceof SpdyGoAwayFrame) {

            SpdyGoAwayFrame spdyGoAwayFrame = (SpdyGoAwayFrame) msg;
            int length = version < 3 ? 4 : 8;
            ChannelBuffer frame = ChannelBuffers.buffer(
                    ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE + length);
            frame.writeShort(version | 0x8000);
            frame.writeShort(SPDY_GOAWAY_FRAME);
            frame.writeInt(length);
            frame.writeInt(spdyGoAwayFrame.getLastGoodStreamId());
            if (version >= 3) {
                frame.writeInt(spdyGoAwayFrame.getStatus().getCode());
            }
            Channels.write(ctx, e.getFuture(), frame, e.getRemoteAddress());
            return;
        }

        if (msg instanceof SpdyHeadersFrame) {

            synchronized (headerBlockEncoder) {
                SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame) msg;
                ChannelBuffer data = headerBlockEncoder.encode(spdyHeadersFrame);
                byte flags = spdyHeadersFrame.isLast() ? SPDY_FLAG_FIN : 0;
                int headerBlockLength = data.readableBytes();
                int length;
                if (version < 3) {
                    length = headerBlockLength == 0 ? 4 : 6 + headerBlockLength;
                } else {
                    length = 4 + headerBlockLength;
                }
                ChannelBuffer frame = ChannelBuffers.buffer(
                        ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE + length);
                frame.writeShort(version | 0x8000);
                frame.writeShort(SPDY_HEADERS_FRAME);
                frame.writeByte(flags);
                frame.writeMedium(length);
                frame.writeInt(spdyHeadersFrame.getStreamId());
                if (version < 3 && data.readableBytes() != 0) {
                    frame.writeShort(0);
                }
                // Writes of compressed data must occur in order
                final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(frame, data);
                Channels.write(ctx, e.getFuture(), buffer, e.getRemoteAddress());
            }
            return;
        }

        if (msg instanceof SpdyWindowUpdateFrame) {
            SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame) msg;
            ChannelBuffer frame = ChannelBuffers.buffer(
                    ByteOrder.BIG_ENDIAN, SPDY_HEADER_SIZE + 8);
            frame.writeShort(version | 0x8000);
            frame.writeShort(SPDY_WINDOW_UPDATE_FRAME);
            frame.writeInt(8);
            frame.writeInt(spdyWindowUpdateFrame.getStreamId());
            frame.writeInt(spdyWindowUpdateFrame.getDeltaWindowSize());
            Channels.write(ctx, e.getFuture(), frame, e.getRemoteAddress());
            return;
        }

        // Unknown message type
        ctx.sendDownstream(evt);
    }
}
