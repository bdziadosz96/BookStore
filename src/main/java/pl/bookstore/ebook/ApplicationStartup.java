package pl.bookstore.ebook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.*;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.*;

@Component
class ApplicationStartup implements CommandLineRunner {

  private final CatalogUseCase catalog;
  private final ManageOrderUseCase manageOrder;
  private final QueryOrderUseCase queryOrder;
  private final String title;
  private final Long limit;
  private final String author;

  public ApplicationStartup(
      CatalogUseCase catalog,
      ManageOrderUseCase placeOrder,
      QueryOrderUseCase queryOrder,
      @Value("${pl.bookstore.query.title:default}") String title,
      @Value("${pl.bookstore.limit:3}") Long limit,
      @Value("${pl.bookstore.query.author:default}") String author) {
    this.catalog = catalog;
    this.manageOrder = placeOrder;
    this.queryOrder = queryOrder;
    this.title = title;
    this.limit = limit;
    this.author = author;
  }

  @Override
  public void run(final String... args) {
    initData();
    searchCatalog();
    placeOrder();
  }

  private void placeOrder() {
    Book bookFromValue =
        catalog
            .findOneByTitle(title)
            .orElseThrow(() -> new IllegalStateException("Cannot find book!"));
    Book bookFromString =
        catalog
            .findOneByTitle("Effective Java")
            .orElseThrow(() -> new IllegalStateException("Cannot find book!"));

    Recipient recipient =
        Recipient.builder()
            .city("Warszawa")
            .email("bdziadosz96@icloud.com")
            .name("Bartek Dziadosz")
            .phone("700-600-500")
            .street("Kwiatowa 18b/10")
            .zipCode("00-000")
            .build();

    PlaceOrderCommand command =
        PlaceOrderCommand.builder()
            .recipient(recipient)
            .item(new OrderItem(bookFromString.getId(), 16))
            .item(new OrderItem(bookFromValue.getId(), 7))
            .build();

    final PlaceOrderResponse response = manageOrder.placeOrder(command);
    System.out.println("Created ORDER with ID " + response.getOrderId());

    queryOrder
        .findAll()
        .forEach(
            order ->
                System.out.println(
                    "GOT ORDER WITH TOTAL PRICE: " + " DETAILS " + order));
  }

  private void initData() {
    catalog.addBook(new CreateBookCommand("Java", "Robert O Brain", 2020, new BigDecimal("20.2")));
    catalog.addBook(
        new CreateBookCommand(
            "Java - Streams and lambda's", "Robert O Brain", 2021, new BigDecimal("39.90")));
    catalog.addBook(
        new CreateBookCommand("Python for beginners", "Kasper Velgert", 2011, new BigDecimal(14)));
    catalog.addBook(
        new CreateBookCommand("Effective Java", "Joshua Bloch", 2018, new BigDecimal(11)));
    catalog.addBook(
        new CreateBookCommand("Clean Code for Java ", "Uncle Bob", 2013, new BigDecimal(12)));
  }

  private void findData() {
    System.out.println("Find by title " + title);
    List<Book> booksByTitle = catalog.findByTitle(title).stream().limit(limit).toList();
    booksByTitle.forEach(System.out::println);
  }

  private void findAndUpdate() {
    System.out.println("Updating book...");
    catalog
        .findOneByAuthorAndTitle("Robert O Brain", "Java")
        .ifPresent(
            book -> {
              final UpdateBookResponse updateBookResponse =
                  catalog.updateBook(
                      UpdateBookCommand.builder()
                          .id(book.getId())
                          .title("Java release 8.0 2021")
                          .build());
              System.out.println("Updating book result is: " + updateBookResponse.success());
            });
  }

  private void searchCatalog() {
    findData();
    findAndUpdate();
    findData();
  }
}
