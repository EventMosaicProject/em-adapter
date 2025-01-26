package com.neighbor.eventmosaic.adapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EmAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmAdapterApplication.class, args);
	}

}
