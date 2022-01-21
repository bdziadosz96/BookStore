package pl.bookstore.ebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.bookstore.ebook.order.domain.OrderProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(OrderProperties.class)
public class EbookApplication {
    public static void main(String[] args) {
        SpringApplication.run(EbookApplication.class, args);
    }
}
