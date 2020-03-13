package com.protobuf;

import com.protobuf.pojo.Global;
import com.protobuf.pojo.ProtoBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;


public class ProtoBufClientHandler extends SimpleChannelInboundHandler<ProtoBuf.Message> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoBuf.Message msg) throws Exception {
        int type = msg.getType();
        if(Global.HEART == type){//心跳包，不做处理
            System.out.println("收到服务器返回的心跳包，不做处理");
        }else if(type == 4){

            System.out.println("关闭连接");
            ctx.channel().close();

        }else{

        }

    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("触发Active事件："+ctx);


        //与服务端建立连接，初始化服务
        String serviceName = "舆情抓取节点1#==#10.1.50.229";

        ProtoBuf.Message msg = ProtoBuf.Message.newBuilder().setType(2).setClientId(ProtoBufClient.clientId).setMsg(serviceName).build();
        ctx.writeAndFlush(msg);

        Thread heatThread = new Thread(new HeartThread(ctx));
        heatThread.setDaemon(true);//心跳线程设为守护线程
        heatThread.start();


    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        System.out.println("等待服务端消息超时，结束连接");
        ProtoBuf.Message closeMsg = ProtoBuf.Message.newBuilder().setType(4).setClientId(ProtoBufClient.clientId).build();
        ctx.writeAndFlush(closeMsg);
        ctx.channel().close();
    }

}