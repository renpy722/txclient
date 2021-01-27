package cn.ren.hanles.txclient.clienk;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端netty
 */
public class ServeBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServeBootstrap.class);

    private int boosGroupThreadNumb;

    private int workGroupThreadNUmb;

    private int port;

    private void setBoosGroupThreadNumb(int boosGroupThreadNumb) {
        this.boosGroupThreadNumb = boosGroupThreadNumb;
    }

    private void setWorkGroupThreadNUmb(int workGroupThreadNUmb) {
        this.workGroupThreadNUmb = workGroupThreadNUmb;
    }

    private void setPort(int port) {
        this.port = port;
    }

    public void start(){
        EventLoopGroup boosGroup = new NioEventLoopGroup(boosGroupThreadNumb);
        EventLoopGroup workGroup = new NioEventLoopGroup(workGroupThreadNUmb);

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置的ServerChannel的一些选项
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))// 设置的ServerChannel的子Channel的选项
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new DelimiterBasedFrameDecoder(8192000, Delimiters.lineDelimiter()));
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new ServerHandler());
                        }
                    });

            ChannelFuture sync = bootstrap.bind(port).sync();
            LOGGER.info("netty server start on port ：{}",port);
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static class ServeBootstrapBuild{

        private int boosGroupThreadNumb;

        private int workGroupThreadNUmb;

        private int port;

        public ServeBootstrapBuild setBoosGroupThreadNumb(int boosGroupThreadNumb) {
            this.boosGroupThreadNumb = boosGroupThreadNumb;
            return this;
        }

        public ServeBootstrapBuild setWorkGroupThreadNUmb(int workGroupThreadNUmb) {
            this.workGroupThreadNUmb = workGroupThreadNUmb;
            return this;
        }

        public ServeBootstrapBuild setPort(int port) {
            this.port = port;
            return this;
        }

        public ServeBootstrap build(){
            ServeBootstrap serveBootstrap = new ServeBootstrap();
            if (boosGroupThreadNumb>0){
                serveBootstrap.setBoosGroupThreadNumb(boosGroupThreadNumb);
            }
            if (workGroupThreadNUmb>0){
                serveBootstrap.setWorkGroupThreadNUmb(workGroupThreadNUmb);
            }
            if (port<=0){
                port = 722;
            }
            serveBootstrap.setPort(port);
            return serveBootstrap;
        }

    }
}
