package cn.com.haohan.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;

public class HttpProxyClientHandle extends ChannelInboundHandlerAdapter {

    private Channel clientChannel;

    public HttpProxyClientHandle(Channel channel){
        this.clientChannel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        FullHttpResponse response = (FullHttpResponse) msg;
        response.headers().add("test","from proxy");
        this.clientChannel.writeAndFlush(response);
    }
}
