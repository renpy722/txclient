# txclient
基于netty的通信组件，落地了应用实现在单server与多client的通信上，主要解决的场景：
1、快速启动Server
2、快速启动client、并进行事件注册
3、提供Server事件发布的能力

#最终此项目可以供给给发布订阅的需求场景


#快速开始

添加依赖
```
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.52.Final</version>
        </dependency>
        <dependency>
            <groupId>cn.ren.hanles</groupId>
            <artifactId>txclient</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```

```
  server的启动
  public static void main(String[] args) {


        new Thread(()->{
            ServeBootstrap.ServeBootstrapBuild build = new ServeBootstrap.ServeBootstrapBuild();
            ServeBootstrap serveBootstrap = build.setPort(9090).setBoosGroupThreadNumb(1).setWorkGroupThreadNUmb(1).build();
            serveBootstrap.start();
        }).start();
        //模拟下发事件
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
```

```
  客户端的启动
  public static void main(String[] args) throws InterruptedException {

        ClientBootstrap.ClientBootstrapBuilder builder = new ClientBootstrap.ClientBootstrapBuilder();
        ClientBootstrap clientBootstrap = builder.setPort(9090).setHost("127.0.0.1").
                setAutoRegire(true).setReConnectTimes(10).build();

        clientBootstrap.start();
        //事件注册
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
```
