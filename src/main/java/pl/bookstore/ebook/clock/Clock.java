package pl.bookstore.ebook.clock;

import java.time.Duration;
import java.time.LocalDateTime;

public interface Clock {
    LocalDateTime now();

    class Fake implements Clock {
        private LocalDateTime time;

        public Fake() {
            this(LocalDateTime.now());
        }

        public Fake(LocalDateTime time) {
            this.time = time;
        }

        @Override
        public LocalDateTime now() {
            return time;
        }

        public void tick(Duration duration) {
            time = time.plus(duration);
        }
    }
}
