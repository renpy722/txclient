package cn.ren.hanles.txclient.clienk;

import cn.ren.hanles.txclient.entity.MessageObject;
import cn.ren.hanles.txclient.entity.MessageType;
import cn.ren.hanles.txclient.util.ClientChennelUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private Gson gson = new Gson();

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        LOGGER.debug("客户端接收到消息：{}",s);
        MessageObject messageObject = gson.fromJson(s, new TypeToken<MessageObject>(){}.getType());
        MessageType messageType = messageObject.getMessageType();
        if (messageType==MessageType.NormalStringMessage){
            LOGGER.info("接收到普通消息"+messageObject.getData().toString());
        }else if (messageType==MessageType.LimitRateChange){
            //TODO 更新配置
            LOGGER.info("接收到限流配置变更消息：{}",gson.fromJson(gson.toJson(messageObject.getData()),MessageType.LimitRateChange.getClazz()));
        }else {
            LOGGER.info("未知类型消息：{}",gson.toJson(messageObject));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ClientChennelUtil.GlobalConnectSuccess = false;
        LOGGER.warn("连接异常：{}",cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ClientChennelUtil.GlobalConnectSuccess = false;
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientChennelUtil.GlobalConnectSuccess = true;
        super.channelActive(ctx);
    }
}
