package bg.credihub.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Value("${payment.service.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return template -> template.header("X-API-KEY", apiKey);
    }
}
