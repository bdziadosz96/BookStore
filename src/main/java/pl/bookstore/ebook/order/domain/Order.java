package pl.bookstore.ebook.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue
  private Long id;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "order_id")
  private List<OrderItem> items;
  private transient Recipient recipient;
  @Builder.Default private OrderStatus status = OrderStatus.NEW;
  private LocalDateTime createdAt;
}

