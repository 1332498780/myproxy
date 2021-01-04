package cn.com.haohan.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

public class HttpProxyServerHandle extends ChannelInboundHandlerAdapter {

    private ChannelFuture cf;
    private String host;
    private int port;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg){
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest)msg;
            String tempHost = request.headers().get("host");
            String[] temp = tempHost.split(":");
            if(temp.length > 1){
                this.port = Integer.valueOf(temp[1]);
            }else{
                if(request.uri().startsWith("https")){
                    this.port = 443;
                }else{
                    this.port = 80;
                }
            }
            this.host = temp[0];
            if("CONNECT".equalsIgnoreCase(request.method().name())){
                HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                ctx.writeAndFlush(httpResponse);
                ctx.pipeline().remove("httpCodec");
                ctx.pipeline().remove("httpObject");
                return;
            }

            //连接到目标服务器
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new HttpProxyInitializer(ctx.channel()));
            ChannelFuture channelFuture = bootstrap.connect(host,port);
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        future.channel().writeAndFlush(msg);
                    }else{
                        ctx.channel().close();
                    }
                }
            });
        }else{
            if(cf == null){
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(ctx.channel().eventLoop())
                        .channel(ctx.channel().getClass())
                        .handler(new ChannelInitializer<Channel>() {
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx0,Object msg){
                                        ctx.channel().writeAndFlush(msg);
                                    }
                                });
                            }
                        });
                cf = bootstrap.connect(host,port);
                cf.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isSuccess()){
                            future.channel().writeAndFlush(msg);
                        }else{
                            future.channel().close();
                        }
                    }
                });
            }else {
                cf.channel().writeAndFlush(msg);
            }
        }

    }
}
