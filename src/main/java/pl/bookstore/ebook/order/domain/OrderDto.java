package pl.bookstore.ebook.order.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import pl.bookstore.ebook.order.price.OrderPrice;

@Value
public class OrderDto {
    Long id;
    OrderStatus status;
    Set<OrderItem> items;
    Recipient recipient;
    LocalDateTime createdAt;
    OrderPrice orderPrice;
    BigDecimal finalPrice;
}
