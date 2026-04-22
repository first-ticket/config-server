package com.first_ticket.config_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.config.server.git.clone-on-start=false",
		"spring.cloud.config.server.git.uri=https://example.com/dummy.git",
		"spring.cloud.config.server.git.username=dummy",
		"spring.cloud.config.server.git.password=dummy",
		"eureka.client.enabled=false"
})
class ConfigServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
