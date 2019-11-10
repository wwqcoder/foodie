package cn.wwq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    //http://localhost:8088/swagger-ui.html  原路径
    //http://localhost:8088/doc.html  优化后的路径

    //配置swagger2核心配置， docket
    @Bean
    public Docket createRestApi(){
        //指定API类型为swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())    //用于定义API汇总信息
                .select()
                .apis(RequestHandlerSelectors.
                        basePackage("cn.wwq.controller"))  //指定controller包
                .paths(PathSelectors.any())  //所有controller
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("天天吃货 电商平台接口API")       //文档标题
                .contact(new Contact("wwq",
                        "https://www.baidu.com",
                        "799819401@qq.com"))    //联系人信息
                .description("专为天天吃货提供的API文档")  //详细信息
                .version("v1.0.1")    //文档版本号
                .termsOfServiceUrl("https://www.baidu.com")  //网站地址
                .build();

    }
}
