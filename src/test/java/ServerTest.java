import cn.ren.hanles.txclient.clienk.ServeBootstrap;
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
                EventSub.pushSubject(EventType.RateLimit, "限流变更: " + new Random().nextInt(10) + "->" + new Random().nextInt(100));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
