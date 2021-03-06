package pl.bookstore.ebook.order.app;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.Delivery;
import pl.bookstore.ebook.order.domain.OrderDto;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.OrderItemCommand;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderCommand;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderResponse;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.UpdateOrderStatusCommand;

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
                .recipient(recipient())
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
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 60))
                .build();

        //when
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> manageOrderService.placeOrder(command));

        //then
        assertTrue(exception.getMessage().contains("Request copies of book "));
    }

    @Test
    public void placeOrderAvailableBooksChange_thenReturnOk() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(recipient())
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
        Long orderId = placeOrder(effectiveJava, javaPuzzlers, "admin@admin.pl");

        Book reducedEffectiveJava = catalogUseCase.findById(effectiveJava.getId()).get();
        Book reducedJavaPuzzlers = catalogUseCase.findById(javaPuzzlers.getId()).get();
        assertEquals(40, reducedEffectiveJava.getAvailable());
        assertEquals(35, reducedJavaPuzzlers.getAvailable());

        //when

        manageOrderService.updateOrderStatus(updateStatusTo(orderId, OrderStatus.CANCELED, "admin@admin.pl"));

        //then
        OrderDto updatedOrder = queryOrderService.findById(orderId).get();
        assertEquals(OrderStatus.CANCELED, updatedOrder.getStatus());
        assertEquals(50, reducedEffectiveJava.getAvailable());
        assertEquals(50, reducedJavaPuzzlers.getAvailable());


    }

    @Test
    public void placeOrderWithPaidStatusCannotRevokeOrder_thenThrowException() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Long orderId = placeOrder(effectiveJava, javaPuzzlers);
        manageOrderService.updateOrderStatus(updateStatusTo(orderId, OrderStatus.PAID, "admin@admin.pl"));

        //when
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> manageOrderService.updateOrderStatus(updateStatusTo(orderId, OrderStatus.CANCELED, "admin@admin.pl")));

        //then
        assertTrue(ex.getMessage().contains("Status cannot be update from: " + OrderStatus.PAID
                + " to: " + OrderStatus.CANCELED));
    }

    @Test
    public void placeOrderWithShippedStatusCannotRevokeOrder_thenThrowException() {
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Long orderId = placeOrder(effectiveJava, javaPuzzlers);
        UpdateOrderStatusCommand commandPaid = updateStatusTo(orderId, OrderStatus.PAID, "admin@admin.pl");
        manageOrderService.updateOrderStatus(commandPaid);

        UpdateOrderStatusCommand commandShipped = updateStatusTo(orderId, OrderStatus.SHIPPED, "admin@admin.pl");
        manageOrderService.updateOrderStatus(commandShipped);

        //when
        UpdateOrderStatusCommand commandCancel = updateStatusTo(orderId, OrderStatus.CANCELED, "admin@admin.pl");
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> manageOrderService.updateOrderStatus(commandCancel));

        //then
        assertTrue(ex.getMessage().contains("Status cannot be update from: " + OrderStatus.SHIPPED
                + " to: " + OrderStatus.CANCELED));
    }

    @Test
    public void placeOrderWithShippedStatusAdminCanRevokeOrder_thenThrowException() {
        Book effectiveJava = givenEffectiveJava();
        String user = "user@example.com";
        Long orderId = placeOrder(effectiveJava, user);

        //when
        String admin = "admin@admin.pl";
        UpdateOrderStatusCommand commandCancel = updateStatusTo(orderId, OrderStatus.CANCELED, admin);
        manageOrderService.updateOrderStatus(commandCancel);

        //then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void placeOrderAdminChangeStatus_thenAvailableBooksCorrect() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Long orderId = placeOrder(effectiveJava);
        assertEquals(40L, availableCopiesOf(effectiveJava));

        //when
        String admin = "admin@admin.pl";
        updateStatusTo(orderId, OrderStatus.PAID, admin);

        //then
        assertEquals(40L, availableCopiesOf(effectiveJava));
    }


    @NotNull
    private UpdateOrderStatusCommand updateStatusTo(Long orderId, OrderStatus orderStatus, String email) {
        return new UpdateOrderStatusCommand(orderId, orderStatus, adminUser());
    }

    private User adminUser() {
        return new User("admin", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }


    @Test
    public void placeOrderWithNotExistingBook_thenThrowException() {
        //given
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .item(new OrderItemCommand(200L, 10))
                .recipient(recipient())
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
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), negativeQuantity))
                .build();
        //when
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> manageOrderService.placeOrder(command));

        //then
        assertTrue(ex.getMessage().contains("Request copies of book " + command.getItems().get(0).getBookId() +
                " requested " + negativeQuantity + " of " + effectiveJava.getAvailable()));
    }


    @Test
    public void placeOrderCannotBeCancelByOtherUser_thenThrowException() {
        Book effectiveJava = givenEffectiveJava();
        String joe = "johndoe@example.com";
        Long orderId = placeOrder(effectiveJava, joe);
        assertEquals(40L, availableCopiesOf(effectiveJava));

        //when
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderId, OrderStatus.CANCELED, user("example@ex.com"));
        manageOrderService.updateOrderStatus(command);

        //then
        assertEquals(40L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
    }

    private User user(String email) {
        return new User(email,"pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void shippingCostsAreDiscountedOver100() {
        //given
        Book book = givenEffectiveJava();

        //when
        Long orderId = placeOrderWithQuantity(book, 10);

        //then
        OrderDto orderDto = orderOf(orderId);
        assertEquals(190, orderDto.getFinalPrice().intValue());
    }

    @Test
    public void shippingCostsAreNotDiscountedLessThan100() {
        //given
        Book book = givenEffectiveJava();

        //when
        Long orderId = placeOrderWithQuantity(book, 3);

        //then
        OrderDto orderDto = orderOf(orderId);
        assertEquals("69.90", orderDto.getFinalPrice().toPlainString());
    }

    @Test
    public void cheapestBookAreNotDiscountedMoreThan400() {
        //given
        Book book = givenEffectiveJava();

        //when
        Long orderId = placeOrderWithQuantity(book, 20);

        //then
        OrderDto orderDto = orderOf(orderId);
        assertEquals("380.00", orderDto.getFinalPrice().toPlainString());
    }

    @Test
    public void cheapestBookAreHalfPriceDiscountedMoreThan200() {
        //given
        Book book = givenEffectiveJava();

        //when
        Long orderId = placeOrderWithQuantity(book, 15);

        //then
        OrderDto orderDto = orderOf(orderId);
        assertEquals("290.00", orderDto.getFinalPrice().toPlainString());
    }

    private OrderDto orderOf(Long orderId) {
        return queryOrderService.findById(orderId).get();
    }

    private Long placeOrderWithQuantity(Book book, int copies) {
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(recipient())
                .delivery(Delivery.COURIER)
                .item(new OrderItemCommand(book.getId(), copies))
                .build();
        return manageOrderService.placeOrder(command).getRight();
    }

    private long availableCopiesOf(Book effectiveJava) {
        return bookJpaRepository.findById(effectiveJava.getId()).get().getAvailable();
    }


    private Long placeOrder(Book effectiveJava, Book javaPuzzlers, String email) {
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(recipient(email))
                .delivery(Delivery.COURIER)
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(javaPuzzlers.getId(), 15))
                .build();
        return manageOrderService.placeOrder(command).getRight();
    }

    private Long placeOrder(Book effectiveJava, Book javaPuzzlers) {
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(javaPuzzlers.getId(), 15))
                .build();
        return manageOrderService.placeOrder(command).getRight();
    }

    private Long placeOrder(Book book) {
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(recipient())
                .item(new OrderItemCommand(book.getId(), 10))
                .build();
        return manageOrderService.placeOrder(command).getRight();
    }

    private Long placeOrder(Book book, String email) {
        PlaceOrderCommand command = PlaceOrderCommand.builder()
                .recipient(recipient(email))
                .delivery(Delivery.COURIER)
                .item(new OrderItemCommand(book.getId(), 10))
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
                new BigDecimal("20"),
                50L
        ));
    }
}