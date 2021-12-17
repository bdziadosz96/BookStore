package pl.bookstore.ebook.order.app;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.db.RecipientJpaRepository;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.db.OrderJpaRepository;
import pl.bookstore.ebook.order.domain.Order;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
class ManageOrderService implements ManageOrderUseCase {
    private final OrderJpaRepository orderRepository;
    private final BookJpaRepository bookRepository;
    private final RecipientJpaRepository recipientRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> items =
                command.getItems()
                        .stream()
                        .map(this::toOrderItem)
                        .collect(Collectors.toSet());
        Order order = Order
                .builder()
                .items(items)
                .recipient(getOrCreateRecipient(command.getRecipient()))
                .build();
        Order save = orderRepository.save(order);
        bookRepository.saveAll(reduceBooks(items));
        ManageOrderService.log.info("Placed order: " + save.getId());
        return PlaceOrderResponse.success(save.getId());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {
        return recipientRepository.findRecipientByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);
    }


    private OrderItem toOrderItem(OrderItemCommand command) {
        Book book = bookRepository.getById(command.getBookId());
        int quantity = command.getQuantity();
        Long available = book.getAvailable();
        if (available >= quantity) {
            return new OrderItem(book, command.getQuantity());
        }
        throw new IllegalStateException("Request copies of book " + book.getId() +
                " requested " + quantity + " of " + book.getAvailable() + " available ");
    }

    @Override
    public void deleteOrderById(Long id) {
        ManageOrderService.log.info("Deleted order with id: " + id);
        orderRepository.deleteById(id);
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        orderRepository.findById(id)
                .ifPresent(order -> {
                    var updateStatusResult = order.updateStatus(status);
                    if (updateStatusResult.isRevoked()) {
                        bookRepository.saveAll(revokeBooks(order.getItems()));

                    }
                    ManageOrderService.log.info("Updated order status " + status + " with id: " + order.getId());
                    orderRepository.save(order);
                });
    }

    private Set<Book> reduceBooks(Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }

    private Set<Book> revokeBooks(Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }
}
