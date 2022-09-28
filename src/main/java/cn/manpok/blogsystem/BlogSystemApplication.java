package cn.manpok.blogsystem;

import cn.manpok.blogsystem.utils.Snowflake;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class BlogSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogSystemApplication.class, args);
    }

    @Bean
    public Snowflake createSnowflake() {
        return new Snowflake();
    }

    @Bean
    public BCryptPasswordEncoder createBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
