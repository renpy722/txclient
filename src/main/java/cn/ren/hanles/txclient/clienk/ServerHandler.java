package cn.ren.hanles.txclient.clienk;

import cn.ren.hanles.txclient.entity.MessageObject;
import cn.ren.hanles.txclient.entity.MessageType;
import cn.ren.hanles.txclient.submod.EventSub;
import cn.ren.hanles.txclient.submod.RegireDetail;
import cn.ren.hanles.txclient.submod.SubjectDetail;
import cn.ren.hanles.txclient.util.Const;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    private Gson gson = new Gson();
    /**
     * 建立连接时发送一条消息
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端请求连接");
        ctx.writeAndFlush("connect to server success ....."+ Const.sendFlag);
    }


    /**
     * 业务处理
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        LOGGER.debug(s);
        MessageObject<RegireDetail> messageObject = gson.fromJson(s, new TypeToken<MessageObject<RegireDetail>>(){}.getType());
        MessageType messageType = messageObject.getMessageType();
        if (messageType==MessageType.NormalStringMessage){
            LOGGER.info("接收到普通消息"+messageObject.getData().toString());
        }else if (messageType==MessageType.LimitRateRegire){
            LOGGER.info("接收到事件消息");
            RegireDetail  regireDetail = messageObject.getData();
            SubjectDetail subjectDetail = new SubjectDetail();
            subjectDetail.setDesc(regireDetail.getDesc());
            subjectDetail.setId(regireDetail.getId());
            subjectDetail.setName(regireDetail.getName());
            subjectDetail.setSubProxy(channelHandlerContext);
            boolean result = EventSub.subjectTopic(regireDetail.getEventType(), subjectDetail);
            LOGGER.info("事件注册结果：{}",result);
        }else {
            LOGGER.warn("未知类型消息，丢弃:{}",gson.toJson(messageObject));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("连接异常：{}",cause.getMessage());
    }
}
