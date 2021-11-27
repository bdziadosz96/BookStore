package pl.bookstore.ebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EbookApplication {

  public static void main(String[] args) {
      SpringApplication.run(EbookApplication.class, args);
  }
}
