package pl.bookstore.ebook.order.app;

import java.math.BigDecimal;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.OrderDto;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.OrderItemCommand;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderCommand;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderResponse;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ManageOrderServiceTest {
    @Autowired
    ManageOrderService manageOrderService;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void placeOrder_thenReturnOk() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(givenRecipent())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(javaPuzzlers.getId(), 15))
                .build();

        //when
        PlaceOrderResponse response = manageOrderService.placeOrder(command);

        //then
        assertTrue(response.isSuccess());
    }


    @Test
    public void placeOrderBooksNotAvailable_thenThrowException() {
        //given
        Book effectiveJava = givenEffectiveJava();
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(givenRecipent())
                .item(new OrderItemCommand(effectiveJava.getId(), 60))
                .build();

        //when
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> manageOrderService.placeOrder(command));

        //then
        assertTrue(exception.getMessage().contains("Request copies of book "));
    }

    @Test
    public void placeOrder_AvailableBooksChange_thenReturnOk() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(givenRecipent())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(javaPuzzlers.getId(), 15))
                .build();

        PlaceOrderResponse response = manageOrderService.placeOrder(command);
        Book reducedEffectiveJava = catalogUseCase.findById(effectiveJava.getId()).get();
        Book reducedJavaPuzzlers = catalogUseCase.findById(javaPuzzlers.getId()).get();

        //then
        assertTrue(response.isSuccess());
        assertEquals(40, reducedEffectiveJava.getAvailable());
        assertEquals(35, reducedJavaPuzzlers.getAvailable());
    }

    @Test
    public void revokeOrder_thenReturnNumberOfAvailableBooksAndCancelledStatus() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Long orderId = placeOrder(effectiveJava, javaPuzzlers);

        Book reducedEffectiveJava = catalogUseCase.findById(effectiveJava.getId()).get();
        Book reducedJavaPuzzlers = catalogUseCase.findById(javaPuzzlers.getId()).get();
        assertEquals(40, reducedEffectiveJava.getAvailable());
        assertEquals(35, reducedJavaPuzzlers.getAvailable());

        //when

        manageOrderService.updateOrderStatus(orderId, OrderStatus.CANCELED);

        //then
        OrderDto updatedOrder = queryOrderService.findById(orderId).get();
        assertEquals(OrderStatus.CANCELED, updatedOrder.getStatus());
        assertEquals(50, reducedEffectiveJava.getAvailable());
        assertEquals(50, reducedJavaPuzzlers.getAvailable());


    }

    @Test
    public void placeOrderWithPaidStatus_CannotRevokeOrder_thenThrowException() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Long orderId = placeOrder(effectiveJava, javaPuzzlers);
        manageOrderService.updateOrderStatus(orderId, OrderStatus.PAID);

        //when
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> manageOrderService.updateOrderStatus(orderId, OrderStatus.CANCELED));

        //then
        assertTrue(ex.getMessage().contains("Status cannot be update from: " + OrderStatus.PAID
                + " to: " + OrderStatus.CANCELED));
    }

    @Test
    public void placeOrderWithShippedStatus_CannotRevokeOrder_thenThrowException() {
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Long orderId = placeOrder(effectiveJava, javaPuzzlers);
        manageOrderService.updateOrderStatus(orderId, OrderStatus.PAID);
        manageOrderService.updateOrderStatus(orderId, OrderStatus.SHIPPED);

        //when
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> manageOrderService.updateOrderStatus(orderId, OrderStatus.CANCELED));

        //then
        assertTrue(ex.getMessage().contains("Status cannot be update from: " + OrderStatus.SHIPPED
                + " to: " + OrderStatus.CANCELED));
    }


    @Test
    public void placeOrderWithNotExistingBook_thenThrowException() {
        //given
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .item(new OrderItemCommand(200L, 10))
                .recipient(givenRecipent())
                .build();

        //when
        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> manageOrderService.placeOrder(command));

        //then
        assertTrue(ex.getMessage().contains("Cannot find books with given id: " + command.getItems().get(0).getBookId()));
    }


    @Test
    public void placeOrderWithNegativeNumberOfBooks_thenThrowException() {
        Book effectiveJava = givenEffectiveJava();
        int negativeQuantity = -10;
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(givenRecipent())
                .item(new OrderItemCommand(effectiveJava.getId(), negativeQuantity))
                .build();
        //when
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> manageOrderService.placeOrder(command));

        //then
        assertTrue(ex.getMessage().contains("Request copies of book " + command.getItems().get(0).getBookId() +
                " requested " + negativeQuantity + " of " + effectiveJava.getAvailable()));
    }

    private Long placeOrder(Book effectiveJava, Book javaPuzzlers) {
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(givenRecipent())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(javaPuzzlers.getId(), 15))
                .build();
        return manageOrderService.placeOrder(command).getRight();
    }


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