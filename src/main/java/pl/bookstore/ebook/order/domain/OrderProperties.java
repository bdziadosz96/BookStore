package pl.bookstore.ebook.order.domain;

import java.time.Duration;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@Value
@ConfigurationProperties("app.time")
public class OrderProperties {
    Duration announcementsLifetime;
    String cronDelayJob;
}
