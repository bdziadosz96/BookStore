package pl.bookstore.ebook;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.bookstore.ebook.catalog.application.controller.CatalogController;
import pl.bookstore.ebook.catalog.domain.model.Book;

@Component
@RequiredArgsConstructor
class ApplicationStartup implements CommandLineRunner {

  private final CatalogController controller;

  @Value("${pl.bookstore.query:default}")
  private String title;

  @Value("${pl.bookstore.limit:3}")
  private Long limit;

  @Override
  public void run(final String... args) throws Exception {
    List<Book> books = controller.findByTitle(title)
            .stream()
            .limit(limit)
            .toList();
    books.forEach(System.out::println);
  }
}
