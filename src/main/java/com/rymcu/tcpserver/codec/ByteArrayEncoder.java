package com.rymcu.tcpserver.codec;

import com.rymcu.tcpserver.instant.ProtoInstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对字节数组进行编码
 *
 * @author 程就人生
 * @date 2020年8月3日
 * @Description
 */
public class ByteArrayEncoder extends MessageToByteEncoder<byte[]> {

    private static Logger log = LoggerFactory.getLogger(ByteArrayEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
        log.info(".....经过ByteArrayEncoder编码.....");
        //字头（1位）
//        out.writeByte(ProtoInstant.FIELD_HEAD);
        //数据长度（2位），字头1位+数据长度2位+数据位(包含校验1位)
//        out.writeShort(ProtoInstant.FILED_LEN + msg.length);
        //消息体，包含我们要发送的数据
        out.writeBytes(msg);
    }

}
