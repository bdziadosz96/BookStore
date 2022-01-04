package pl.bookstore.ebook.order.app.port;

import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.clock.Clock;
import pl.bookstore.ebook.order.app.ManageOrderService;
import pl.bookstore.ebook.order.app.QueryOrderService;
import pl.bookstore.ebook.order.db.OrderJpaRepository;
import pl.bookstore.ebook.order.domain.Delivery;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.*;

@SpringBootTest(
        properties = "app.time.announcements-lifetime=1H"
)
class AbandonedOrdersJobTest {
    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock.Fake clock() {
            return new Clock.Fake();
        }
    }

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private ManageOrderService manageOrderService;

    @Autowired
    private AbandonedOrdersJob ordersJob;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private QueryOrderService orderService;

    @Autowired
    private CatalogUseCase catalogUseCase;

    @Autowired
    Clock.Fake clock;

    @Test
    public void orderShouldBeMarkedAsAbandoned() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Long orderId = placeOrder(effectiveJava, 15);
        assertEquals(35, availableCopiesOf(effectiveJava));
        //when
        clock.tick(Duration.ofHours(2));
        ordersJob.run();

        //then
        assertEquals(OrderStatus.ABANDONED, orderService.findById(orderId).get().getStatus());
        assertEquals(50, availableCopiesOf(effectiveJava));
    }

    private Long placeOrder(Book book, int quantity) {
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(recipient())
                .delivery(Delivery.COURIER)
                .item(new OrderItemCommand(book.getId(), quantity))
                .build();
        return manageOrderService.placeOrder(command).getRight();
    }

    private Recipient recipient(String mail) {
        return Recipient.builder().email(mail).build();
    }


    private Recipient recipient() {
        return Recipient.builder()
                .name("Jan")
                .city("Warszawa")
                .phone("111222333")
                .email("Janek@test.pl")
                .zipCode("0123")
                .street("Testowa")
                .build();
    }

    private Book givenEffectiveJava() {
        return bookJpaRepository.save(new Book(
                "Effective Java",
                2005,
                new BigDecimal("19.99"),
                50L
        ));
    }

    private long availableCopiesOf(Book effectiveJava) {
        return bookJpaRepository.findById(effectiveJava.getId()).get().getAvailable();
    }

}