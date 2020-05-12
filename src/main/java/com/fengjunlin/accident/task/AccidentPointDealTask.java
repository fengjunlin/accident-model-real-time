package com.fengjunlin.accident.task;

import com.fengjunlin.accident.model.AccidentModel;
import com.fengjunlin.accident.service.AccidentPointDeal;
import com.fengjunlin.accident.tools.BaseDataAndAccidentCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description 处理实时计算出来的事故点
 * @Author fengjl
 * @Date 2019/9/2 14:29
 * @Version 1.0
 **/
@Component
@Async
public class AccidentPointDealTask {
    private static final Logger logger = LoggerFactory.getLogger(AccidentPointDealTask.class);
    @Autowired
    AccidentPointDeal accidentPointDeal;

    @Scheduled(fixedDelay = 1000 * 300)
    public void DealAccidentScheduled() {
        logger.info("************** 碰撞点位置校验开始运行 **************");
        try {
            List<AccidentModel> accidentList = BaseDataAndAccidentCache.accidentList;
            logger.info("accidentList size: " + accidentList.size());
            accidentPointDeal.excludeAccidentPoint();
            logger.info("accidentList size: " + accidentList.size());
            logger.info("************** 碰撞点位置校验结束 **************");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

