package com.protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ProtoBufClient {

    public static final int clientId = 666;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            //加载Initializer
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                           .channel(NioSocketChannel.class)
                           .handler(new ProtoBufClientInitializer());

            //连接服务端
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8899).sync();
            channelFuture.channel().closeFuture().sync();
        }
        finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
