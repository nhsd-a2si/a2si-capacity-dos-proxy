package com.nhsd.a2si;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ImportResource;

@EnableZuulProxy
@SpringBootApplication()
public class DosProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DosProxyApplication.class, args);
	}
}
