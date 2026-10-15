package com.jxbg.ticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.jxbg.ticket.mapper")
@EnableRabbit
@EnableScheduling
@SpringBootApplication
public class TicketBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketBootApplication.class, args);
    }
}
