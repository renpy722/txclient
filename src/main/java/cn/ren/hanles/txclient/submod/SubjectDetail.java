package cn.ren.hanles.txclient.submod;

import io.netty.channel.ChannelHandlerContext;
public class SubjectDetail {

    /**
     * 订阅者Id，唯一不重复
     */
    private String id;

    /**
     * 订阅者名称
     */
    private String name;

    /**
     * 订阅描述
     */
    private String desc;

    /**
     * 远端订阅者代理通信对象
     */
    private ChannelHandlerContext subProxy;

    public ChannelHandlerContext getSubProxy() {
        return subProxy;
    }

    public void setSubProxy(ChannelHandlerContext subProxy) {
        this.subProxy = subProxy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
