package com.season;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class MycloudTestApplication implements ApplicationListener<ApplicationStartedEvent>{

	@Value("${my.val}")
	String val;

	public static void main(String[] args) {
		SpringApplication.run(MycloudTestApplication.class, args);
	}

	@Override
	public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
		System.out.println("获取到的值："+val);
	}
}
