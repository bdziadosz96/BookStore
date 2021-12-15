package pl.bookstore.ebook.order.app;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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

@Service
@AllArgsConstructor
@Transactional
class ManageOrderService implements ManageOrderUseCase {
    private final OrderJpaRepository orderRepository;
    private final BookJpaRepository bookRepository;
    private final RecipientJpaRepository recipientRepository;

    @Override
    public PlaceOrderResponse placeOrder(final PlaceOrderCommand command) {
        final Set<OrderItem> items =
                command.getItems()
                        .stream()
                        .map(this::toOrderItem)
                        .collect(Collectors.toSet());
        final Order order = Order
                .builder()
                .items(items)
                .recipient(getOrCreateRecipient(command.getRecipient()))
                .build();
        final Order save = orderRepository.save(order);
        bookRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.success(save.getId());
    }

    private Recipient getOrCreateRecipient(final Recipient recipient) {
        return recipientRepository.findRecipientByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);
    }


    private OrderItem toOrderItem(final OrderItemCommand command) {
        final Book book = bookRepository.getById(command.getBookId());
        final int quantity = command.getQuantity();
        final Long available = book.getAvailable();
        if (available >= quantity) {
            return new OrderItem(book, command.getQuantity());
        }
        throw new IllegalStateException("Request copies of book " + book.getId() +
                " requested " + quantity + " of " + book.getAvailable() + " available ");
    }

    @Override
    public void deleteOrderById(final Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void updateOrderStatus(final Long id, final OrderStatus status) {
        orderRepository.findById(id)
                .ifPresent(order -> {
                    final var updateStatusResult = order.updateStatus(status);
                    bookRepository.saveAll(revokeBooks(order.getItems()));
                    orderRepository.save(order);
                });
    }

    private Set<Book> reduceBooks(final Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    final Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }

    private Set<Book> revokeBooks(final Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    final Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }
}
