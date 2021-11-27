package pl.bookstore.ebook.order.app.port;

import lombok.Value;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {
    List<OrderDto> findAll();

    Optional<OrderDto> findById(Long id);

    @Value
    class OrderDto {
        Long id;
        OrderStatus status;
        List<OrderItemDto> items;
        Recipient recipient;
        LocalDateTime createdAt;

        public BigDecimal totalPrice() {
            return items.stream()
                    .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    @Value
    class OrderItemDto {
        Book book;
        int quantity;
    }
}
