import cn.ren.hanles.txclient.clienk.ClientBootstrap;
import cn.ren.hanles.txclient.entity.MessageObject;
import cn.ren.hanles.txclient.entity.MessageType;
import cn.ren.hanles.txclient.entity.QpsReport;
import cn.ren.hanles.txclient.submod.EventType;
import cn.ren.hanles.txclient.submod.RegireDetail;
import cn.ren.hanles.txclient.util.ClientChennelUtil;

import java.util.Random;

public class ClientTest {

    public static void main(String[] args) throws InterruptedException {

        ClientBootstrap.ClientBootstrapBuilder builder = new ClientBootstrap.ClientBootstrapBuilder();
        ClientBootstrap clientBootstrap = builder.setPort(9090).setHost("127.0.0.1").
                setAutoRegire(true).setReConnectTimes(10).build();

        clientBootstrap.start();

        RegireDetail regireDetail = new RegireDetail();
        regireDetail.setEventType(EventType.RateLimit);
        regireDetail.setId("123");
        Thread.sleep(4000);
        clientBootstrap.addRegisterSub(regireDetail,2);
        System.out.println("注册完成");

        new Thread(()->{
            System.out.println("模拟上报QPS。。。。。。。。。。。");
            while (true){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MessageObject<QpsReport> message = new MessageObject<>();
                message.setMessageType(MessageType.QpsReport);
                QpsReport report = new QpsReport();
                report.setClientId("13");
                report.setEventType(EventType.RateLimit);
                report.setQps(new Random().nextInt(20));
                message.setData(report);
                ClientChennelUtil.sendMessage(message);
            }
        }).start();
    }
}
