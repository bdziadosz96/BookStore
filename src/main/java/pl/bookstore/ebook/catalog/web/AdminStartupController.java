package pl.bookstore.ebook.catalog.web;

import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase.CreateBookCommand;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.OrderItemCommand;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderCommand;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderResponse;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.Recipient;

@Slf4j
@RequestMapping("/admin")
@RestController
@AllArgsConstructor
class AdminStartupController {

    private final CatalogUseCase catalog;
    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;

    @PostMapping("/init")
    @Transactional()
    public void initalizeData() {
        initData();
        placeOrder();
    }

    private void placeOrder() {
        Book effective_java =
                catalog
                        .findOneByTitle("Effective Java")
                        .orElseThrow(() -> new IllegalStateException("Cannot find book!"));
        Book clean_code =
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
                        .item(new OrderItemCommand(effective_java.getId(), 16))
                        .item(new OrderItemCommand(clean_code.getId(), 7))
                        .build();

        PlaceOrderResponse response = manageOrder.placeOrder(command);
        String result = response.handle(
                orderId -> "Created ORDER with id: " + orderId,
                error -> "Failed to created order: " + error
        );
        AdminStartupController.log.info(result);

        queryOrder.findAll()
                .forEach(order -> AdminStartupController.log.info("GET ORDER WITH TOTAL PRICE " + order.totalPrice()));
    }

    private void initData() {
        Author sampleOne = new Author("Joshua", "Bloch");
        Author sampleTwo = new Author("Uncle", "Bob");
        authorRepository.save(sampleOne);
        authorRepository.save(sampleTwo);

        CreateBookCommand effectiveJava =
                new CreateBookCommand(
                        "Effective Java",
                        Set.of(sampleOne.getId()),
                        2005,
                        new BigDecimal("80.00"),
                        50L);
        CreateBookCommand cleanJava =
                new CreateBookCommand(
                        "Java - Clean Architecture",
                        Set.of(sampleOne.getId(), sampleTwo.getId()),
                        2011,
                        new BigDecimal("64.00"),
                        50L);

        catalog.addBook(effectiveJava);
        catalog.addBook(cleanJava);
    }
}
