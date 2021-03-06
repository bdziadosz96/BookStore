package pl.bookstore.ebook.commons;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
class RestTemplateFounder {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
