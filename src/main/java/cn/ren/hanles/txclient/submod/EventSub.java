package cn.ren.hanles.txclient.submod;

import cn.ren.hanles.txclient.util.Const;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 事件总线
 */
public class EventSub {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSub.class);

    private static final Gson gson = new Gson();

    private static Map<EventType, List<SubjectDetail>> contain = new ConcurrentHashMap<>();

    public static Map<EventType, List<SubjectDetail>> getContain(){
        return contain;
    }

    /**
     * 事件订阅
     * @param type
     * @param detail
     * @return
     */
    public static boolean subjectTopic(EventType type,SubjectDetail detail){
        if (!contain.containsKey(type)){
            contain.put(type,new ArrayList<>());
        }
        synchronized (type){
            Set<String> idSet = contain.get(type).stream().map(i -> i.getId()).collect(Collectors.toSet());
            if (idSet.contains(detail.getId())){
                LOGGER.info("事件注册失败，Id重复：{}",detail.getId());
                return false;
            }
            contain.get(type).add(detail);
            LOGGER.info("事件注册成功，Id：{}，name：{}",detail.getId());
            return true;
        }
    }

    /**
     * 事件订阅取消
     * @param type
     * @param detail
     * @return
     */
    public static boolean unSubjectTopic(EventType type,SubjectDetail detail){
        return false;
    }

    /**
     * 事件发布
     * @param type
     * @param content
     * @param <T>
     */
    public static <T> void pushSubject(EventType type,T content){

        try{

            List<SubjectDetail> list = contain.get(type);
            if (list!=null && list.size()>0){
                LOGGER.info("事件总线-推送事件，类型：{}内容：{}",type,gson.toJson(content));
                list.forEach(item -> item.getSubProxy().writeAndFlush(content+ Const.sendFlag));
            }
        }catch (Exception e){
            LOGGER.warn("事件发布失败：",e);
        }
    }
}
