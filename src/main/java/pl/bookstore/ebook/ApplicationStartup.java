package pl.bookstore.ebook;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Set;

import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.*;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.*;

@Component
@AllArgsConstructor
class ApplicationStartup implements CommandLineRunner {

  private final CatalogUseCase catalog;
  private final ManageOrderUseCase manageOrder;
  private final QueryOrderUseCase queryOrder;
  private final AuthorJpaRepository authorRepository;

  @Override
  public void run(final String... args) {
    initData();
    placeOrder();
  }

  private void placeOrder() {
    final Book effective_java =
        catalog
            .findOneByTitle("Effective Java")
            .orElseThrow(() -> new IllegalStateException("Cannot find book!"));
    final Book clean_code =
            catalog
                    .findOneByTitle("Java - Clean Architecture")
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
            .item(new OrderItem(effective_java.getId(), 16))
            .build();

    final PlaceOrderResponse response = manageOrder.placeOrder(command);
    System.out.println("Created ORDER with ID " + response.getOrderId());

    queryOrder
        .findAll()
        .forEach(
            order ->
                System.out.println(
                    "GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS " + order));
  }

  private void initData() {
    Author sampleOne = new Author("Joshua", "Bloch");
    Author sampleTwo = new Author("Uncle", "Bob");
    authorRepository.save(sampleOne);
    authorRepository.save(sampleTwo);

    final CreateBookCommand effectiveJava =
        new CreateBookCommand("Effective Java", Set.of(sampleOne.getId()), 2005, new BigDecimal("80.00"));
    final CreateBookCommand cleanJava =
        new CreateBookCommand("Java - Clean Architecture", Set.of(sampleOne.getId(),sampleTwo.getId()), 2011, new BigDecimal("64.00"));

    catalog.addBook(effectiveJava);
    catalog.addBook(cleanJava);
  }
}
