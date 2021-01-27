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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 客户端启动类
 */
public class ClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);


    private int port;

    private String host;

    private Gson gson = new Gson();

    //用户自动注册的注册实体
    private RegireDetail regireObj;
    //连接恢复时是否进行事件自动注册
    private boolean connectRecoverAutoRegire = false;
    //自动重连次数，默认3次
    private int reConnectTimes = 3;
    //当前执行重连的次数
    private int nowReConnectTime =0;

    public void setReConnectTimes(int reConnectTimes) {
        this.reConnectTimes = reConnectTimes;
    }

    public void setConnectRecoverAutoRegire(boolean connectRecoverAutoRegire) {
        this.connectRecoverAutoRegire = connectRecoverAutoRegire;
    }

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
            EventLoopGroup workGroup = new NioEventLoopGroup(1);
            try{
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE,true)
                        .option(ChannelOption.TCP_NODELAY,true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            protected void initChannel(SocketChannel sc) throws Exception {
                                ChannelPipeline pipeline = sc.pipeline();
                                pipeline.addLast(new DelimiterBasedFrameDecoder(8192000, Delimiters.lineDelimiter()));
                                pipeline.addLast(new StringDecoder());
                                pipeline.addLast(new StringEncoder());
                                pipeline.addLast(new ClientHandler());
                            }
                        });


                while (true){
                    if (nowReConnectTime>=reConnectTimes){
                        break;
                    }
                    connectServer(bootstrap);
                    Thread.sleep(5000);
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                workGroup.shutdownGracefully();
            }
        }).start();
    }

    private void connectServer(Bootstrap bootstrap) throws InterruptedException {
        try{

            ChannelFuture connect = bootstrap.connect(host, port);
            Channel ch = connect.sync().channel();
            ClientChennelUtil.setChannel(ch);

            new Thread(()->{
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ch.isActive() && ch.isOpen()){
                    nowReConnectTime=0;
                }
                if (ch.isActive() && ch.isOpen() & connectRecoverAutoRegire && Objects.nonNull(regireObj)){
                    LOGGER.info("连接成功，自动进行事件注册");
                    addRegisterSub(regireObj,2);
                }else {
                    LOGGER.error("未检测到连接恢复，或者没有配置事件注册，取消自动事件注册恢复");
                }
            }).start();

            ch.closeFuture().sync();
        }catch (Exception e){
            nowReConnectTime+=1;
            LOGGER.info("5秒后尝试连接服务器........");
        }
    }

    /**
     * 事件注册订阅
     * @param regireDetail
     * @param time
     */
    public boolean addRegisterSub(RegireDetail regireDetail, int time){
        long endTime = System.currentTimeMillis()+1000*time;
        while (!ClientChennelUtil.GlobalConnectSuccess && System.currentTimeMillis()<=endTime){
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (ClientChennelUtil.GlobalConnectSuccess){
            MessageObject<RegireDetail> messageObject = new MessageObject<>();
            messageObject.setData(regireDetail);
            messageObject.setMessageType(MessageType.LimitRateRegire);
            ClientChennelUtil.sendMessage(messageObject);
            //注册成功时备份regireObj
            regireObj = regireDetail;
            return true;
        }
        return false;
    }

    public static class ClientBootstrapBuilder{

        private int port;

        private String host;

        private boolean autoRegire;

        private int reConnectTimes;

        public ClientBootstrapBuilder setReConnectTimes(int reConnectTimes) {
            this.reConnectTimes = reConnectTimes;
            return this;
        }

        public ClientBootstrapBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public ClientBootstrapBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public ClientBootstrapBuilder setAutoRegire(boolean autoRegire) {
            this.autoRegire = autoRegire;
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
            if (autoRegire){
                obj.setConnectRecoverAutoRegire(autoRegire);
            }
            if (reConnectTimes>0){
                obj.setReConnectTimes(reConnectTimes);
            }
            return obj;
        }
    }

}


