package com.example.web.job;

import com.example.web.service.IntegralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IntegralMockJob {


//    @Autowired
//    private IntegralService _IntegralService;

//    /**
//     * 每隔5s赠送积分
//     */
//    @Scheduled(cron = "*/10 * * * * ?")
//    private void MockIntegralValue() {
//        long nowDateTime = System.currentTimeMillis();
//        System.out.println("固定定时任务执行:--->" + nowDateTime + "，任务名称:每隔5s赠送积分");
//        _IntegralService.MockIntegralValue();
//    }

}
