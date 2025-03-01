package cn.ren.hanles.txclient.clienk;

import cn.ren.hanles.txclient.entity.MessageObject;
import cn.ren.hanles.txclient.entity.MessageType;
import cn.ren.hanles.txclient.entity.QpsReport;
import cn.ren.hanles.txclient.submod.EventSub;
import cn.ren.hanles.txclient.submod.EventType;
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        MessageObject<String> init = new MessageObject<>();
        init.setMessageType(MessageType.NormalStringMessage);
        init.setData("connect to server success .....");
        ctx.writeAndFlush(gson.toJson(init)+ Const.sendFlag);
    }


    /**
     * 业务处理
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        LOGGER.debug(s);
        MessageObject messageObject = gson.fromJson(s, new TypeToken<MessageObject>(){}.getType());
        MessageType messageType = messageObject.getMessageType();
        if (messageType==MessageType.NormalStringMessage){
            LOGGER.info("接收到普通消息"+messageObject.getData().toString());
        }else if (messageType==MessageType.LimitRateRegire){
            LOGGER.info("接收到事件消息");
            RegireDetail  regireDetail = gson.fromJson(gson.toJson(messageObject.getData()),RegireDetail.class);
            SubjectDetail subjectDetail = new SubjectDetail();
            subjectDetail.setDesc(regireDetail.getDesc());
            subjectDetail.setId(regireDetail.getId());
            subjectDetail.setName(regireDetail.getName());
            subjectDetail.setSubProxy(channelHandlerContext);
            boolean result = EventSub.subjectTopic(regireDetail.getEventType(), subjectDetail);
            LOGGER.info("事件注册结果：{}",result);
        }else if (messageType==MessageType.QpsReport){
            QpsReport qpsReport = gson.fromJson(gson.toJson(messageObject.getData()),QpsReport.class);
            LOGGER.info("接收到qps上报消息：客户ID：{}，qps：{}",qpsReport.getClientId(),qpsReport.getQps());
        }else {
            LOGGER.warn("未知类型消息，丢弃:{}",gson.toJson(messageObject));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("连接异常：{}",cause.getMessage());
        try{
            LOGGER.info("连接异常尝试移除事件");
            Map<EventType, List<SubjectDetail>> contain = EventSub.getContain();
            contain.values().stream().forEach(list ->{
                Iterator<SubjectDetail> iterator = list.iterator();
                while (iterator.hasNext()){
                    SubjectDetail next = iterator.next();
                    if (next.getSubProxy().equals(ctx)){
                        iterator.remove();
                        LOGGER.info("连接异常尝试移除事件，移除成功");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
