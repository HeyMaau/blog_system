package cn.manpok.blogsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
@EnableScheduling
public class SpringThreadPoolConfig {

    /*private final int CORE_POOL_SIZE = 2;
    private final int MAX_POOL_SIZE = 30;
    private final int QUEUE_CAPACITY = 10;
    private final String THREAD_NAME_PREFIX = "blog-system-async-thread";*/

    /*@Bean(name = "asyncTaskServiceExecutor")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 设置任务拒绝策略
        */

    /**
     * 4种
     * ThreadPoolExecutor类有几个内部实现类来处理这类情况：
     * - AbortPolicy 丢弃任务，抛RejectedExecutionException
     * - CallerRunsPolicy 由该线程调用线程运行。直接调用Runnable的run方法运行。
     * - DiscardPolicy  抛弃策略，直接丢弃这个新提交的任务
     * - DiscardOldestPolicy 抛弃旧任务策略，从队列中踢出最先进入队列（最后一个执行）的任务
     * 实现RejectedExecutionHandler接口，可自定义处理器
     *//*
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }*/

    @Bean(name = "asyncTaskServiceExecutor")
    public Executor virtualThreadAsyncServiceExecutor() {
        return new TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("virtual-async#", 1).factory()));
    }
}
