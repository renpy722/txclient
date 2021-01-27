package cn.ren.hanles.txclient.entity;
import cn.ren.hanles.txclient.submod.RegireDetail;

public enum  MessageType {

    LimitRateRegire("regire",RegireDetail.class,"事件注册"),
    NormalStringMessage("normalStr",String.class,"普通字符串消息"),
    LimitRateChange("limitChange",LimitChangeEntity.class,"限流配置更新"),
    QpsReport("qpsReport",QpsReport.class,"客户端QPS上报");

    private String type;
    private Class clazz;
    private String desc;

    MessageType(String type, Class clazz, String desc) {
        this.type = type;
        this.clazz = clazz;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
