package com.anran.cloudlibrary;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@EnableSwagger2WebMvc
@MapperScan("com.anran.cloudlibrary.mapper")
//@EnableAspectJAutoProxy(exposeProxy = true)
public class CloudLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudLibraryApplication.class, args);
    }

}
