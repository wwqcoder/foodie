package cn.wwq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan(basePackages = "cn.wwq.mapper")
//扫描所有包以及相关使用包
@ComponentScan(basePackages = {"cn.wwq","org.n3r.idworker"})
@EnableScheduling   //开启定时任务
@EnableRedisHttpSession  //开启使用redis 作为Springsession
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}
