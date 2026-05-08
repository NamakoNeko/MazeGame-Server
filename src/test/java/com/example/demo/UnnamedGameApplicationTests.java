package com.example.demo;

import com.javaclass.game.UnnamedGameApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
	classes = UnnamedGameApplication.class,
	properties = {
		"spring.profiles.active=test",
		"spring.datasource.url=jdbc:h2:mem:unnamedgame;MODE=MySQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"init.admin.account=admin",
		"init.admin.password=admin",
		"jwt.secret=0123456789abcdef0123456789abcdef"
	}
)
class UnnamedGameApplicationTests {

	@Test
	void contextLoads() {
	}

}
