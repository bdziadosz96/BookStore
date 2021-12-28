package pl.bookstore.ebook.order.app;

import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.domain.Recipient;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.OrderItemCommand;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderCommand;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderResponse;

@DataJpaTest
@Import({ManageOrderService.class})
class ManageOrderServiceTest {
    @Autowired
    ManageOrderService service;

    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Test
    void placeOrder() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(givenRecipent())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(javaPuzzlers.getId(), 15))
                .build();
        //when
        PlaceOrderResponse response = service.placeOrder(command);
        //then
        assertTrue(response.isSuccess());
    }

    @NotNull
    private Recipient givenRecipent() {
        return Recipient.builder()
                .name("Jan")
                .city("Warszawa")
                .phone("111222333")
                .email("Janek@test.pl")
                .zipCode("0123")
                .street("Testowa")
                .build();
    }

    private Book givenJavaPuzzlers() {
        return bookJpaRepository.save(new Book("Java Puzzlers",
                2010,
                new BigDecimal("39.99"),
                50L));
    }

    private Book givenEffectiveJava() {
        return bookJpaRepository.save(new Book(
                "Effective Java",
                2005,
                new BigDecimal("19.99"),
                50L
        ));
    }
}