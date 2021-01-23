import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 建立连接时发送一条消息
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端请求连接");
        ctx.writeAndFlush("connect to server success .....\r\n");
    }


    /**
     * 业务处理
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
    }
}
