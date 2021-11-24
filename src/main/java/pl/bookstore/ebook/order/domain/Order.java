package pl.bookstore.ebook.order.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Order {
  private Long id;
  private List<OrderItem> items;
  private Recipient recipient;
  @Builder.Default private OrderStatus status = OrderStatus.NEW;
  private LocalDateTime createdAt;
}
