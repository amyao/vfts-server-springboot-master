package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Axl
 * main()
 */
@SpringBootApplication(scanBasePackages = "com.vfts")
@SpringBootConfiguration
@EnableScheduling
@MapperScan(basePackages = {"com.vfts.trade.dao", "com.vfts.user.dao"})
public class vftsApplication {
    public static void main(String[] args) {
        SpringApplication.run(vftsApplication.class, args);
    }
}