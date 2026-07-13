package bg.credihub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CredihubApplication {

	public static void main(String[] args) {
		SpringApplication.run(CredihubApplication.class, args);
	}

}
