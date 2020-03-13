package com.heart.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Random;

public class HeartBeatClient  {

    int port;
    Channel channel;

    public HeartBeatClient(int port){
        this.port = port;
    }
    public static void main(String[] args) throws Exception{
        HeartBeatClient client = new HeartBeatClient(8090);
        client.start();
    }

    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new HeartBeatClientInitializer());

            connect(bootstrap,port);
            String  text = "I am alive";
            int count = 0;
            while (channel.isActive()){
                sendMsg(text + count++);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void connect(Bootstrap bootstrap,int port) throws Exception{
        channel = bootstrap.connect("localhost",8090).sync().channel();
    }

    public void sendMsg(String text) throws Exception{



        int random = (int)(Math.random()*100);
        System.out.println(random);

        Thread.sleep(3000);//每隔3s发送一次心跳

        if(random%2==0){
            channel.writeAndFlush(text);
        }
    }

    static class HeartBeatClientInitializer extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("decoder", new StringDecoder());
            pipeline.addLast("encoder", new StringEncoder());
            pipeline.addLast(new HeartBeatClientHandler());
        }
    }

    static class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(" 收到服务端消息 :" +msg);
            if(msg!= null && msg.equals("you are out")) {
                System.out.println(" server closed connection , so client will close too");
                ctx.channel().closeFuture();
            }
        }
    }
}