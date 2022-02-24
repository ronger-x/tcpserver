package com.rymcu.tcpserver.handler;

import com.rymcu.tcpserver.instant.ProtoInstant;
import com.rymcu.tcpserver.util.CharacterConvert;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 心跳处理
 *
 * @author 程就人生
 * @date 2020年8月6日
 * @Description
 */
public class HeartBeatServerHandler extends IdleStateHandler {

    private static Logger log = LoggerFactory.getLogger(HeartBeatServerHandler.class);

    private static final int READ_IDLE_GAP = 150;

    private Set<SocketChannel> socketChannels = new HashSet<>();

    public HeartBeatServerHandler() {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        SocketChannel channel = (SocketChannel) context.channel();
        log.info("客户端连接上服务端");
        log.info("RemoteAddress:" + channel.remoteAddress());
        if (!socketChannels.contains(channel)) {
            socketChannels.add(channel);
            InputStream inputStream = null;
            ByteArrayOutputStream bytOutputStream = null;
            try {
                ClassPathResource classPathResource = new ClassPathResource("Fruit-18.png");
                log.info("fileName:{},fileSize:{}", classPathResource.getFilename(), classPathResource.contentLength());
                String message = classPathResource.getFilename() + "##" + classPathResource.contentLength();
                channel.writeAndFlush(message.getBytes(StandardCharsets.UTF_8));
                Thread.sleep(200);
                inputStream = classPathResource.getInputStream();
                //输出流
                bytOutputStream = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = inputStream.read(b)) != -1) {
                    bytOutputStream.write(b, 0, n);
                }
                byte[] bytes = bytOutputStream.toByteArray();
                channel.writeAndFlush(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    bytOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        SocketChannel channel = (SocketChannel) context.channel();
        socketChannels.remove(channel);
        log.info("客户端断开连接{}", channel.localAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断消息实例
        if (null == msg || !(msg instanceof byte[])) {
            super.channelRead(ctx, msg);
            return;
        }
        byte[] data = (byte[]) msg;
        int dataLength = data.length;
        ByteBuf buf = Unpooled.buffer(dataLength);
        buf.writeBytes(data);
        int type = CharacterConvert.byteToInt(buf.readByte());
        //机器编号
        int deviceId = CharacterConvert.byteToInt(buf.readByte());
        //如果是管理后台登录操作时
        if (type == ProtoInstant.HEART_BEAT) {
            int verify = CharacterConvert.byteToInt(buf.readByte());
            int sum = CharacterConvert.sum(ProtoInstant.FIELD_HEAD, dataLength + ProtoInstant.FILED_LEN, type, deviceId);
            if (verify != CharacterConvert.getLow8(sum)) {
                log.error("心跳包，校验位错误！机器编码：" + deviceId);
            } else {
                log.info("接收到心跳信息" + deviceId);
                if (ctx.channel().isActive()) {
                    ctx.writeAndFlush(msg);
                }
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        //log.info(READ_IDLE_GAP + "秒内未读到数据!");
    }
}
