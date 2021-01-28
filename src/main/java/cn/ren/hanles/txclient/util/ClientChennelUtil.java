package cn.ren.hanles.txclient.util;

import cn.ren.hanles.txclient.entity.MessageObject;
import com.google.gson.Gson;
import io.netty.channel.Channel;

/**
 * 客户端向服务端进行主动消息通信的实体
 */
public class ClientChennelUtil {
    private static Channel channel;
    public static volatile boolean GlobalConnectSuccess = false;
    //注册成功状态
    public static volatile boolean GlobalRegireSuccess = false;

    private static Gson gson = new Gson();
    public static void setChannel(Channel channel) {
        ClientChennelUtil.channel = channel;
    }

    public static void sendMessage(MessageObject messageObject){
        String content = gson.toJson(messageObject);
        channel.writeAndFlush(content+Const.sendFlag);
    }
}
