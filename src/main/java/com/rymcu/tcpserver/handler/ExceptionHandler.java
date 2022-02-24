package com.rymcu.tcpserver.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 服务端异常处理handler
 * @author 程就人生
 * @date 2019年11月12日
 * @Description
 *
 */
@ChannelHandler.Sharable
@Service("exceptionHandler")
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    private static Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof Exception) {
            //捕捉异常信息
            cause.printStackTrace();
            log.error(cause.getMessage());
            ctx.close();
        } else {
            //捕捉异常信息
            cause.printStackTrace();
            log.error(cause.getMessage());
            ctx.close();
        }
    }

    /**
     * 通道 Read 读取 Complete 完成
     * 做刷新操作 ctx.flush()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
