package pl.bookstore.ebook;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.bookstore.ebook.catalog.application.controller.CatalogController;
import pl.bookstore.ebook.catalog.domain.model.Book;

@Component
@RequiredArgsConstructor
class ApplicationStartup implements CommandLineRunner {

  private final CatalogController controller;

  @Override
  public void run(final String... args) throws Exception {
    List<Book> books = controller.findByTitle("Pan");
    books.forEach(System.out::println);
  }
}
