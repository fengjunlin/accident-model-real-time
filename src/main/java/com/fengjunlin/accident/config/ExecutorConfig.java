package com.fengjunlin.accident.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description 开启定时任务的线程池
 * @Author fengjl
 * @Date 2019/7/1 16:06
 * @Version 1.0
 **/
@Configuration
@EnableAsync
public class ExecutorConfig {
    @Bean("asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数，如果池中的实际线程数小于corePoolSize,无论是否其中有空闲的线程，都会给新的任务产生新的线程
        executor.setCorePoolSize(5);
        //配置最大线程数，如果池中的线程数 >corePoolSize and <maximumPoolSize,而又有空闲线程，就给新任务使用空闲线程，如没有空闲线程，则产生新线程
        executor.setMaxPoolSize(10);
        //配置队列大小，如果池中的线程数＝maximumPoolSize，则有空闲线程使用空闲线程，否则新任务放入workQueue。
        executor.setQueueCapacity(1024);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("asyncExecutor-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
