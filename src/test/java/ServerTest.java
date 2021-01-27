import cn.ren.hanles.txclient.clienk.ServeBootstrap;
import cn.ren.hanles.txclient.entity.LimitChangeEntity;
import cn.ren.hanles.txclient.entity.MessageObject;
import cn.ren.hanles.txclient.entity.MessageType;
import cn.ren.hanles.txclient.submod.EventSub;
import cn.ren.hanles.txclient.submod.EventType;

import java.util.Random;

public class ServerTest {

    public static void main(String[] args) {


        new Thread(()->{
            ServeBootstrap.ServeBootstrapBuild build = new ServeBootstrap.ServeBootstrapBuild();
            ServeBootstrap serveBootstrap = build.setPort(9090).setBoosGroupThreadNumb(1).setWorkGroupThreadNUmb(1).build();
            serveBootstrap.start();
        }).start();

        new Thread(()->{
            while (true) {
                MessageObject<LimitChangeEntity> messageObject = new MessageObject<>();
                messageObject.setMessageType(MessageType.LimitRateChange);
                LimitChangeEntity entity = new LimitChangeEntity();
                entity.setLimitKey("cn.rtl.simple.limitTest.LimitTestController_testRate");
                entity.setLimitCount(200);
                messageObject.setData(entity);
                EventSub.pushSubject(EventType.RateLimit, messageObject);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
