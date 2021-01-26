package cn.ren.hanles.txclient.entity;

/**
 * 消息传输实体
 */
public class MessageObject<T> {

    /**
     * 消息类型
     * @Link MessageType
     */
    private MessageType messageType;

    /**
     * 针对不同类型消息的数据实体
     */
    private T data;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
