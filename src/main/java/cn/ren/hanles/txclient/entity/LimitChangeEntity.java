package cn.ren.hanles.txclient.entity;

/**
 * 限流更新配置下发实体
 */
public class LimitChangeEntity {

    /**
     * 限流接口唯一标识
     */
    private String limitKey;

    /**
     * 限流次数更改为
     */
    private int limitCount;

    /**
     * 排除某个监听者，如果该Id与接收者自身Id一致则跳过【多个需要跳过的逗号分割】
     */
    private String excludeIds;

    public String getLimitKey() {
        return limitKey;
    }

    public void setLimitKey(String limitKey) {
        this.limitKey = limitKey;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public String getExcludeIds() {
        return excludeIds;
    }

    public void setExcludeIds(String excludeIds) {
        this.excludeIds = excludeIds;
    }
}
