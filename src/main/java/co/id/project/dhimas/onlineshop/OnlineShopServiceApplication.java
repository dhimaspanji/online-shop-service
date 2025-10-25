package co.id.project.dhimas.onlineshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("co.id.project.dhimas.onlineshop.exception.config")
public class OnlineShopServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineShopServiceApplication.class, args);
	}

}
