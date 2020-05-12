package com.fengjunlin.accident.task;

import com.fengjunlin.accident.model.BaseDataPointWithSpeed;
import com.fengjunlin.accident.tools.BaseDataAndAccidentCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * @Description 用于定时清除内存中自己缓存的数据，防止数据量特别大的时候，造成内存泄漏
 * @Author fengjl
 * @Date 2019/8/28 16:41
 * @Version 1.0
 **/
@Component
@Async
public class ClearTask {
    private static final Logger logger = LoggerFactory.getLogger(ClearTask.class);
    @Scheduled(fixedDelay = 1000 * 300)
    public void clearScheduled() {
        logger.info("************** 定时清理垃圾线程开启 **************");
        try {
            Map<String, List<BaseDataPointWithSpeed>> map = BaseDataAndAccidentCache.map;
            logger.info("************** map size "+map.size()+" **************");
            for(Map.Entry<String, List<BaseDataPointWithSpeed>> entry : map.entrySet()) {
                String key = entry.getKey();
                List<BaseDataPointWithSpeed> value = entry.getValue();
                BaseDataPointWithSpeed baseDataPointWithSpeed = value.get(0);
                double timeCurrentSystemTime = baseDataPointWithSpeed.getTimeCurrentSystemTime(System.currentTimeMillis());
                if (timeCurrentSystemTime > 300) {
                    map.remove(key);
                    logger.info("************** 定时清理垃圾线程 deviceId: "+key+"   timeDiffer:"+timeCurrentSystemTime+" **************");
                }
            }
            logger.info("************** map size "+map.size()+" **************");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

