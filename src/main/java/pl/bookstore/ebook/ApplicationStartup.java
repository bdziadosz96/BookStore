package pl.bookstore.ebook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.model.Book;

import java.util.List;

import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.*;

@Component
class ApplicationStartup implements CommandLineRunner {

  private final CatalogUseCase catalog;
  private final String title;
  private final Long limit;
  private final String author;

  public ApplicationStartup(
      CatalogUseCase catalog,
      @Value("${pl.bookstore.query.title:default}") String title,
      @Value("${pl.bookstore.limit:3}") Long limit,
      @Value("${pl.bookstore.query.author:default}") String author) {
    this.catalog = catalog;
    this.title = title;
    this.limit = limit;
    this.author = author;
  }

  @Override
  public void run(final String... args) throws Exception {
    initData();
    findData();
    findAndUpdate();
    findData();
  }

  private void initData() {
    catalog.addBook(new CommandCreateBook("Think in Java", "Robert O Brain", 2020));
    catalog.addBook(new CommandCreateBook("Python for beginners", "Kasper Velgert", 2011));
    catalog.addBook(new CommandCreateBook("Effective Java", "Joshua Bloch", 2018));
    catalog.addBook(new CommandCreateBook("Clean Code", "Uncle Bob", 2013));
  }

  private void findData() {
    System.out.println("Find by title " + title);
    List<Book> booksByTitle = catalog.findByTitle(title).stream().limit(limit).toList();
    booksByTitle.forEach(System.out::println);
  }

  private void findAndUpdate() {
    System.out.println("Updating book...");
    catalog
        .findOneByAuthorAndTitle("Robert O Brain", "Think in Java")
        .ifPresent(
            book -> {
              final UpdateBookResponse updateBookResponse =
                  catalog.updateBook(
                      new CommandUpdateBook(
                          book.getId(),
                          "Think in Java version 8.0 after update",
                          book.getAuthor(),
                          book.getYear()));
              System.out.println("Updating book result is: " + updateBookResponse.success());
            });
  }
}
