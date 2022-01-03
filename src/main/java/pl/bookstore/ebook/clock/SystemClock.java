package pl.bookstore.ebook.clock;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
class SystemClock implements Clock {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
