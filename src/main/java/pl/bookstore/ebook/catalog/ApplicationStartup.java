package pl.bookstore.ebook.catalog;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.bookstore.ebook.catalog.app.controller.CatalogController;
import pl.bookstore.ebook.catalog.domain.model.Book;

@Component
@RequiredArgsConstructor
class ApplicationStartup implements CommandLineRunner {

  private final CatalogController controller;

  @Value("${pl.bookstore.query.title:default}")
  private String title;

  @Value("${pl.bookstore.limit:3}")
  private Long limit;

  @Value("${pl.bookstore.query.author:default}")
  private String author;

  @Override
  public void run(final String... args) throws Exception {
    System.out.println("Find by author " + author);
    List<Book> booksByAuthor = controller.findByAuthor(author).stream().limit(limit).toList();
    booksByAuthor.forEach(System.out::println);

    System.out.println("Find by title " + title);
    List<Book> booksByTitle = controller.findByTitle(title).stream().limit(limit).toList();
    booksByTitle.forEach(System.out::println);
  }
}
