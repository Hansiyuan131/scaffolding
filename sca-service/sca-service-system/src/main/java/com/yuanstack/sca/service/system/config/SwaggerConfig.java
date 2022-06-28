package com.yuanstack.sca.service.system.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @description: 自动生成API文档和在线接口调试工具
 * @author: hansiyuan
 * @date: 2022/6/28 3:16 PM
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig {

    @Bean
    public Docket createRestApi(Environment environment) {
        // TODO 环境隔离
        //设置显示的swagger环境信息,判断是否处在自己设定的环境当中,为了安全生产环境不开放Swagger
//        Profiles profiles = Profiles.of("dev", "test");
//        boolean flag = environment.acceptsProfiles(profiles);
        boolean flag = true;
        //创建一个Docket的对象，相当于是swagger的一个实例
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("1.x版本")
                .apiInfo(apiInfo())
                // 只有当springboot配置文件为dev或test环境时，才开启swaggerAPI文档功能
                .enable(flag)
                .select()
                // 这里指定Controller扫描包路径:设置要扫描的接口类，一般是Controller类
                // 这里采用包扫描的方式来确定要显示的接口
                .apis(RequestHandlerSelectors.basePackage("com.yuanstack.sca.service.system.controller"))
                // 这里采用包含注解的方式来确定要显示的接口
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 配置过滤哪些，设置对应的路径才获取
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 配置相关的api信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .description("System API调试文档")
                //作者信息
                .contact(new Contact("yuan", "https://blog.csdn.net/qq_35093132", "yuanstack@163.com"))
                .version("v1.0")
                .title("System API调试文档")
                //服务Url
                .termsOfServiceUrl("")
                .build();
    }
}
