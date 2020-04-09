package com.steven.financial.stock.util;

import com.steven.financial.stock.service.StockDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class ScheduleTask {


//    @Scheduled(cron = "0/10 * * * * *")
//    public void test() {
//        Thread thread = Thread.currentThread();
//        String name = thread.getName();
//        System.out.println("测试1："+name);
//        logger.info("定时任务1:"+ new SimpleDateFormat("HH:mm:ss").format(new Date()));
//    }

    @Autowired
    private StockDBService stockDBService;

    @Scheduled(cron = "0 0 16 ? * 1-5")
    public void saveIntoDB() {
        log.info("start to run insert process......");
        try {
            stockDBService.addDataIntoDB("sh600667", "60", "no", "1023");
        } catch (Exception e) {
            log.error("[schedule task]error occurring when inserting to DB", e);
        }

    }
}
