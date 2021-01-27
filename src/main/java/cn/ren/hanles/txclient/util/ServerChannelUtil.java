package cn.ren.hanles.txclient.util;

import cn.ren.hanles.txclient.entity.MessageObject;
import cn.ren.hanles.txclient.entity.MessageType;
import cn.ren.hanles.txclient.submod.EventSub;
import cn.ren.hanles.txclient.submod.EventType;
import cn.ren.hanles.txclient.submod.SubjectDetail;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * server主动向client通信使用的工具类，目前仅支持字符串消息通信，后续其他对象类型消息通信时，扩展此处的方法，扩展MessageType进行支持
 */
public class ServerChannelUtil {

    private static final Gson gson = new Gson();
    /**
     * 向注册指定事件的某个Id的客户端发送字符串消息
     * @param eventType
     * @param clientId
     */
    public static boolean sendStringMessage(EventType eventType, String clientId, String messageContent){
        MessageObject<String> message = new MessageObject<>();
        message.setMessageType(MessageType.NormalStringMessage);
        message.setData(messageContent);
        List<SubjectDetail> detailList = EventSub.getContain().get(eventType);
        if (detailList!=null){
            List<SubjectDetail> oneList = detailList.stream().filter(i -> i.getId().equals(clientId)).collect(Collectors.toList());
            if (oneList != null && oneList.size()>0){
                oneList.get(0).getSubProxy().writeAndFlush(gson.toJson(message)+Const.sendFlag);
                return true;
            }
            return false;
        }
        return false;
    }
}
