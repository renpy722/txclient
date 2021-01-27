package cn.ren.hanles.txclient.intec;

import cn.ren.hanles.txclient.entity.LimitChangeEntity;
import cn.ren.hanles.txclient.entity.MessageObject;

public interface LimitConfigUpdate {

    public boolean updateConfig(MessageObject<LimitChangeEntity> config);
}
