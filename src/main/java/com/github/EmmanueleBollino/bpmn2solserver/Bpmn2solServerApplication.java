package com.github.EmmanueleBollino.bpmn2solserver;

import com.github.EmmanueleBollino.bpmn2solserver.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class Bpmn2solServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Bpmn2solServerApplication.class, args);
	}
}
