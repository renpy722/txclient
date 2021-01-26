import cn.ren.hanles.txclient.submod.EventSub;
import cn.ren.hanles.txclient.submod.EventType;
import cn.ren.hanles.txclient.util.ServerChannelUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.internal.ChannelUtils;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class EchoServer {

	public static int DEFAULT_PORT = 7;

	public static void main(String[] args) throws Exception {
		int port;

		try {
			port = Integer.parseInt(args[0]);
		} catch (RuntimeException ex) {
			port = DEFAULT_PORT;
		}

		// 多线程事件循环器
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // boss
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // worker
		
		try {
			// 启动NIO服务的引导程序类
			ServerBootstrap b = new ServerBootstrap();
			
			b.group(bossGroup, workerGroup) // 设置EventLoopGroup
			.channel(NioServerSocketChannel.class) // 指明新的Channel的类型
			.option(ChannelOption.SO_BACKLOG, 128) // 设置的ServerChannel的一些选项
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.handler(new LoggingHandler(LogLevel.DEBUG))// 设置的ServerChannel的子Channel的选项
			.childHandler(new ServerInitialize())
			;
 
			// 绑定端口，开始接收进来的连接
			ChannelFuture f = b.bind(port).sync();

			System.out.println("EchoServer已启动，端口：" + port);
			// 等待服务器 socket 关闭 。
			// 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
			/*new Thread(()->{
				while (true){
					try {
						Thread.sleep(2000);
						ServerChannelUtil.getAllChannel().keySet().forEach(item ->{
							ServerChannelUtil.getChannel(item).writeAndFlush("server send message:"+UUID.randomUUID().toString()+"\r\n");
						});
						System.out.println("一次触发完成");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();*/
			Channel channel = f.channel();
			while (true){
				Thread.sleep(3000);
				EventSub.pushSubject(EventType.RateLimit,"更新限流配置 1->"+ new Random(10).nextInt());
				if (1==0){
					break;
				}
			}

			channel.closeFuture().sync();
			System.out.println("-----");
		} finally {

			// 优雅的关闭
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}

	}
}