import cn.ren.hanles.txclient.clienk.ClientBootstrap;
import cn.ren.hanles.txclient.submod.EventType;
import cn.ren.hanles.txclient.submod.RegireDetail;

public class ClientTest {

    public static void main(String[] args) throws InterruptedException {

        ClientBootstrap.ClientBootstrapBuilder builder = new ClientBootstrap.ClientBootstrapBuilder();
        ClientBootstrap clientBootstrap = builder.setPort(9090).setHost("127.0.0.1").build();

        clientBootstrap.start();

        RegireDetail regireDetail = new RegireDetail();
        regireDetail.setEventType(EventType.RateLimit);
        regireDetail.setId("123");
        Thread.sleep(4000);
        clientBootstrap.addRegisterSub(regireDetail,2);
        System.out.println("注册完成");
    }
}
