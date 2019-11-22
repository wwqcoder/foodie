package cn.wwq.config;

import cn.wwq.service.OrderService;
import cn.wwq.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    /**
     * 使用定时任务关闭超期未支付订单的弊端
     *
     * 1。 会有时间差，程序不严谨
     *     10:39下单 ，11:00检查不足一个小时，12：00检查，超过一个小时余39分
     * 2。不支持集群
     *    单机没毛病，使用集群后就会有多个定时任务，
     *    解决方案：只使用一台计算机节点来执行定时任务
     *
     * 3。会对数据库全表搜索，及其影响数据库性能
     *
     * 定时任务，仅仅只适用于小型轻量级项目，传统项目
     *
     * 消息队列：MQ Kafka
     *       延时任务（队列）
     *
     *       10：12分下单的，未付款   11：12分检查
     *
     *
     */

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void autoCloseOrder(){
        orderService.closeOrder();
        System.out.println("执行定时任务，当前时间为："+
                DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
    }
}
