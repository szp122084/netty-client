package com.protobuf;

import com.protobuf.pojo.ProtoBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

public class HeartThread implements Runnable {

    private ChannelHandlerContext ctx;

    public HeartThread(ChannelHandlerContext ctx){
        this.ctx = ctx;
    }
    @Override
    public void run() {
        try {
            ProtoBuf.Message heartMsg = ProtoBuf.Message.newBuilder().setType(1).build();
            while (ctx.channel().isActive()==true){//连接存活，且心跳连续丢失次数小于3次

                ctx.writeAndFlush(heartMsg);
                System.out.println("心跳已发送");

//                int random = (int)(Math.random()*10000);
//                System.out.println("客户端随机等待时间："+random);

                Thread.sleep(2000);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("客户端心跳发送已停止");
    }
}
