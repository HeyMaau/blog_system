package cn.manpok.blogsystem.config;

import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class TomcatConfiguration {

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {

        return protocolHandler -> {
            // 创建 OfVirtual，指定虚拟线程名称的前缀，以及线程编号起始值
            Thread.Builder.OfVirtual ofVirtual = Thread.ofVirtual().name("virtualthread#", 1);
            // 获取虚拟线程池工厂
            ThreadFactory factory = ofVirtual.factory();
            // 通过该工厂，创建 ExecutorService
            protocolHandler.setExecutor(Executors.newThreadPerTaskExecutor(factory));
        };
    }
}
