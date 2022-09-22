package cn.manpok.blogsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    /**
     * 创建用户API信息
     *
     * @return
     */
    @Bean
    public Docket createUserApi() {
        return new Docket(DocumentationType.SWAGGER_2)  // DocumentationType.SWAGGER_2 固定的，代表swagger2
                .groupName("用户API") // 如果配置多个文档的时候，那么需要配置groupName来分组标识
                .apiInfo(userApiInfo()) // 用于生成API信息
                .select() // select()函数返回一个ApiSelectorBuilder实例,用来控制接口被swagger做成文档
                .apis(RequestHandlerSelectors.basePackage("cn.manpok.blogsystem.controller.user")) // 用于指定扫描哪个包下的接口
                .paths(PathSelectors.any())// 选择所有的API,如果你想只为部分API生成文档，可以配置这里
                .build();
    }

    /**
     * 用户API信息
     *
     * @return
     */
    private ApiInfo userApiInfo() {
        return new ApiInfoBuilder()
                .title("博客系统用户API") //  可以用来自定义API的主标题
                .description("博客系统项目用户API管理") // 可以用来描述整体的API
                .termsOfServiceUrl("") // 用于定义服务的域名
                .version("1.0") // 可以用来定义版本。
                .build(); //
    }

    /**
     * 创建管理API信息
     *
     * @return
     */
    @Bean
    public Docket createAdminApi() {
        return new Docket(DocumentationType.SWAGGER_2)  // DocumentationType.SWAGGER_2 固定的，代表swagger2
                .groupName("管理API") // 如果配置多个文档的时候，那么需要配置groupName来分组标识
                .apiInfo(adminApiInfo()) // 用于生成API信息
                .select() // select()函数返回一个ApiSelectorBuilder实例,用来控制接口被swagger做成文档
                .apis(RequestHandlerSelectors.basePackage("cn.manpok.blogsystem.controller.admin")) // 用于指定扫描哪个包下的接口
                .paths(PathSelectors.any())// 选择所有的API,如果你想只为部分API生成文档，可以配置这里
                .build();
    }

    /**
     * 管理API信息
     *
     * @return
     */
    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("博客系统管理API") //  可以用来自定义API的主标题
                .description("博客系统项目管理API管理") // 可以用来描述整体的API
                .termsOfServiceUrl("") // 用于定义服务的域名
                .version("1.0") // 可以用来定义版本。
                .build(); //
    }

    /**
     * 创建门户API信息
     *
     * @return
     */
    @Bean
    public Docket createPortalApi() {
        return new Docket(DocumentationType.SWAGGER_2)  // DocumentationType.SWAGGER_2 固定的，代表swagger2
                .groupName("门户API") // 如果配置多个文档的时候，那么需要配置groupName来分组标识
                .apiInfo(portalApiInfo()) // 用于生成API信息
                .select() // select()函数返回一个ApiSelectorBuilder实例,用来控制接口被swagger做成文档
                .apis(RequestHandlerSelectors.basePackage("cn.manpok.blogsystem.controller.portal")) // 用于指定扫描哪个包下的接口
                .paths(PathSelectors.any())// 选择所有的API,如果你想只为部分API生成文档，可以配置这里
                .build();
    }

    /**
     * 门户API信息
     *
     * @return
     */
    private ApiInfo portalApiInfo() {
        return new ApiInfoBuilder()
                .title("博客系统门户API") //  可以用来自定义API的主标题
                .description("博客系统项目门户API管理") // 可以用来描述整体的API
                .termsOfServiceUrl("") // 用于定义服务的域名
                .version("1.0") // 可以用来定义版本。
                .build(); //
    }
}
