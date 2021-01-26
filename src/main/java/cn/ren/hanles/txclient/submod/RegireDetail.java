package cn.ren.hanles.txclient.submod;

/**
 * 订阅注册消息体
 */
public class RegireDetail{

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
     * 订阅类型
     */
    private EventType eventType;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventTypes) {
        eventType = eventTypes;
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
