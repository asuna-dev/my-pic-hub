package org.zepe.pichub;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = ShardingSphereAutoConfiguration.class)
@MapperScan("org.zepe.pichub.mapper")
public class PicHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicHubApplication.class, args);
    }

}
