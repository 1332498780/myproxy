package cn.com.haohan.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpProxyInitializer extends ChannelInitializer {

    private Channel clientChnnel;

    public HttpProxyInitializer(Channel channel){
        this.clientChnnel = channel;
    }

    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(6553600));
        ch.pipeline().addLast(new HttpProxyClientHandle(this.clientChnnel));
    }
}
