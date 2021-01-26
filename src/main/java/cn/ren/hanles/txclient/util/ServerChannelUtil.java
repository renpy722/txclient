package cn.ren.hanles.txclient.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerChannelUtil {

    private static Map<String, ChannelHandlerContext> channel_map = new ConcurrentHashMap<>();

    public static void addChannel(String code,ChannelHandlerContext channel){

        channel_map.put(code,channel);
    }

    public static Map<String, ChannelHandlerContext> getAllChannel() {
        return channel_map;
    }

    public static ChannelHandlerContext getChannel(String code){
        return channel_map.get(code);
    }
}
