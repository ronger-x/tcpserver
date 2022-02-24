package com.rymcu.tcpserver;

import com.rymcu.tcpserver.server.SocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TcpserverApplication {

    public static void main(String[] args) {

        //获取 application 的上下文
        ApplicationContext applicationContext = new SpringApplicationBuilder(TcpserverApplication.class).web(WebApplicationType.NONE).run(args);
        /**
         * 启动netty TCP 的服务端
         */
        SocketServer socketServer = applicationContext.getBean(SocketServer.class);
        socketServer.start();
    }

}
