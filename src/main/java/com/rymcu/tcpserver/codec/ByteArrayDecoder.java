package com.rymcu.tcpserver.codec;

import com.rymcu.tcpserver.instant.ProtoInstant;
import com.rymcu.tcpserver.util.CharacterConvert;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * byte     1字节    （8位） -27~27-1 0 Byte 255
 * short    2字节    （16位） -215~215-1 0 Short
 * int      4字节    （32位） -231~ 231-1 0 Integer
 * long     8字节    （64位） -263~263-1 0 Long
 * char     2字节    （C语言中是1字节）可以存储一个汉字
 * float    4字节    （32位） -3.4e+38 ~ 3.4e+38 0.0f Float
 * double   8字节    （64位） -1.7e+308 ~ 1.7e+308 0 Double
 * char 2字节（16位） u0000~uFFFF（‘’~‘？’） ‘0’ Character （0~216-1（65535））
 * 布尔 boolean 1/8字节（1位） true, false FALSE Boolean
 * C语言中，short、int、float、long、double，分别为：1个、2个、4个、8个、16个
 * 对字节数组进行解码
 *
 * @author 程就人生
 * @date 2020年8月3日
 * @Description
 */
public class ByteArrayDecoder extends ByteToMessageDecoder {

    private static Logger log = LoggerFactory.getLogger(ByteArrayDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 标记一下当前的readIndex的位置
        in.markReaderIndex();
        //判断获取到的数据是否够字头，不沟通字头继续往下读
        //字头：1位，数据串总长度：2位
        if (in.readableBytes() < ProtoInstant.FILED_LEN) {
            log.info("不够包头，继续读！");
            return;
        }
        //读取字头1位
        int fieldHead = CharacterConvert.byteToInt(in.readByte());
        if (fieldHead != ProtoInstant.FIELD_HEAD) {
            String error = "字头不对:" + ctx.channel().remoteAddress();
            log.info(error);
            ctx.close();
            return;
        }
        //长度2位，读取传送过来的消息的长度。
        int length = CharacterConvert.shortToInt(in.readShort());
        // 长度如果小于0
        if (length < 0) {
            // 非法数据，关闭连接
            log.info("数据长度为0，非法数据，关闭连接！");
            ctx.close();
            return;
        }
        // 读到的消息体长度如果小于传送过来的消息长度,减去字头1位，数据长度2位
        int dataLength = length - ProtoInstant.FILED_LEN;
        if (dataLength > in.readableBytes()) {
            // 重置读取位置
            in.resetReaderIndex();
            return;
        }
        byte[] array;
        if (in.hasArray()) {
            log.info("堆缓冲");
            // 堆缓冲
            ByteBuf slice = in.slice();
            array = slice.array();
        } else {
            log.info("直接缓冲");
            // 直接缓冲
            array = new byte[dataLength];
            in.readBytes(array, 0, dataLength);
        }
        if (array.length > 0) {
            in.retain();
            out.add(array);
        }
    }
}
