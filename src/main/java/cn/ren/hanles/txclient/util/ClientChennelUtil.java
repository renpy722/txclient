package cn.ren.hanles.txclient.util;

import cn.ren.hanles.txclient.entity.MessageObject;
import com.google.gson.Gson;
import io.netty.channel.Channel;

public class ClientChennelUtil {
    private static Channel channel;
    private static Gson gson = new Gson();
    public static void setChannel(Channel channel) {
        ClientChennelUtil.channel = channel;
    }

    public static void sendMessage(MessageObject messageObject){
        String content = gson.toJson(messageObject);
        channel.writeAndFlush(content+"\r\n");
    }
}
