package com.abernathy.webinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.abernathy")
public class WebInterfaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebInterfaceApplication.class, args);
	}

}
