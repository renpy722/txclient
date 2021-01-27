package cn.ren.hanles.txclient.entity;

import cn.ren.hanles.txclient.submod.EventType;

/**
 * QPS 数据上报实体
 */
public class QpsReport {

    private EventType eventType;

    private String clientId;

    private int qps;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getQps() {
        return qps;
    }

    public void setQps(int qps) {
        this.qps = qps;
    }
}
