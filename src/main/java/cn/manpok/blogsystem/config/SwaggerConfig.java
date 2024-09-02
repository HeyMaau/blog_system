package cn.manpok.blogsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("卧卷")
                        .description("后台API文档")
                        .version("v1.3"));
    }

    @Bean
    public GroupedOpenApi portalApiInfo() {
        return GroupedOpenApi.builder()
                .group("门户API")
                .pathsToMatch("/portal/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApiInfo() {
        return GroupedOpenApi.builder()
                .group("管理API")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApiInfo() {
        return GroupedOpenApi.builder()
                .group("用户API")
                .pathsToMatch("/user/**")
                .build();
    }
}
