package com.daqsoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * 配置
 *
 * @author liaoxiaoxia
 * @version 1.0.0
 * @date 2018-9-13 16:31
 * @since JDK 1.8
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("分词处理")
                        .description("分词处理")
                        .build())
                .groupName("分词处理")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.daqsoft.controller"))
                .paths(PathSelectors.any())
                .build();

    }
}