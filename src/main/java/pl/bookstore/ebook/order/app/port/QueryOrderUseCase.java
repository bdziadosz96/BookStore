package pl.bookstore.ebook.order.app.port;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Value;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

public interface QueryOrderUseCase {
    List<OrderDto> findAll();

    Optional<OrderDto> findById(Long id);

    @Value
    class OrderDto {
        Long id;
        OrderStatus status;
        Set<OrderItem> items;
        Recipient recipient;
        LocalDateTime createdAt;


        public BigDecimal totalPrice() {
            return items.stream()
                    .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

    }
}
