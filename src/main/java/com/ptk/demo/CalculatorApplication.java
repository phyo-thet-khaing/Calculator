package com.ptk.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

@SpringBootApplication
@EnableCaching
public class CalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalculatorApplication.class, args);
	}
	
	@Bean
	public HazelcastInstance hazelcastInstance() 
	{
	      ClientConfig clientConfig = new ClientConfig();
	      clientConfig.getNetworkConfig().addAddress("hazelcast:5701");

	      return HazelcastClient.newHazelcastClient(clientConfig);
	  }

}
