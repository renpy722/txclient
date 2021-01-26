package cn.ren.hanles.txclient.clienk;

import cn.ren.hanles.txclient.entity.MessageObject;
import cn.ren.hanles.txclient.entity.MessageType;
import cn.ren.hanles.txclient.submod.RegireDetail;
import cn.ren.hanles.txclient.util.ClientChennelUtil;
import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 客户端启动类
 */
public class ClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

    public volatile boolean GlobalConnectSuccess = false;

    private int port;

    private String host;

    private Gson gson = new Gson();

    private static Channel channel = null;


    private void setPort(int port) {
        this.port = port;
    }


    private void setHost(String host) {
        this.host = host;
    }

    private ClientBootstrap() {
    }

    public void start(){
        new Thread(()->{
            EventLoopGroup workGroup = new NioEventLoopGroup();
            try{
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE,true)
                        .option(ChannelOption.TCP_NODELAY,true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            protected void initChannel(SocketChannel sc) throws Exception {
                                ChannelPipeline pipeline = sc.pipeline();
                                pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                                pipeline.addLast(new StringDecoder());
                                pipeline.addLast(new StringEncoder());
                                pipeline.addLast(new ClientHandler());
                            }
                        });

                ChannelFuture connect = bootstrap.connect(host, port);
                Channel ch = connect.sync().channel();
                ClientChennelUtil.setChannel(ch);
                GlobalConnectSuccess = true;
                ch.closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                workGroup.shutdownGracefully();
            }
        }).start();
    }

    /**
     * 事件注册订阅
     * @param regireDetail
     * @param time
     */
    public boolean addRegisterSub(RegireDetail regireDetail, int time){
        long endTime = System.currentTimeMillis()+1000*time;
        while (!GlobalConnectSuccess && System.currentTimeMillis()<=endTime){
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (GlobalConnectSuccess){
            MessageObject<RegireDetail> messageObject = new MessageObject<>();
            messageObject.setData(regireDetail);
            messageObject.setMessageType(MessageType.LimitRateRegire);
            ClientChennelUtil.sendMessage(messageObject);
            return true;
        }
        return false;
    }

    public static class ClientBootstrapBuilder{

        private int port;

        private String host;

        public ClientBootstrapBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public ClientBootstrapBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public ClientBootstrap build(){
            ClientBootstrap obj = new ClientBootstrap();
            if (this.host!=null){
                obj.setHost(this.host);
            }
            if (this.port>0){
                obj.setPort(this.port);
            }
            return obj;
        }
    }

}


