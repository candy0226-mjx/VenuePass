package com.jxbg.ticket.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String JWT_SCHEME_NAME = "Bearer Token";

    @Bean
    public OpenAPI ticketOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("场馆预约与核销系统接口文档")
                        .description("包含用户认证、场馆资源、预约订单、支付流水、核销、点评审核、工作台统计等接口。")
                        .version("1.0.0")
                        .contact(new Contact().name("jxbg")))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(JWT_SCHEME_NAME, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("01-用户认证")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi venueApi() {
        return GroupedOpenApi.builder()
                .group("02-场馆资源")
                .pathsToMatch("/venues/**", "/areas/**", "/activities/**", "/sessions/**", "/price-rules/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("03-预约订单")
                .pathsToMatch("/orders/**", "/payments/**", "/export/**")
                .build();
    }

    @Bean
    public GroupedOpenApi checkinApi() {
        return GroupedOpenApi.builder()
                .group("04-核销管理")
                .pathsToMatch("/checkin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reviewApi() {
        return GroupedOpenApi.builder()
                .group("05-点评管理")
                .pathsToMatch("/reviews/**", "/uploads/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("06-后台管理")
                .pathsToMatch("/dashboard/**", "/operation-logs/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationApi() {
        return GroupedOpenApi.builder()
                .group("07-站内通知")
                .pathsToMatch("/notifications/**")
                .build();
    }
}
