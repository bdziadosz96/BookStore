package pl.bookstore.ebook.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.bookstore.ebook.jpa.BaseEntity;

import static javax.persistence.EnumType.STRING;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity {
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Set<OrderItem> items;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Recipient recipient;

    @Builder.Default
    @Enumerated(STRING)
    private OrderStatus status = OrderStatus.NEW;

    @CreatedDate private LocalDateTime createdAt;

    @Builder.Default
    @Enumerated(STRING)
    private Delivery delivery = Delivery.COURIER;

    @LastModifiedDate private LocalDateTime updatedAt;

    public UpdateStatusResult updateStatus(OrderStatus newStatus) {
        UpdateStatusResult result = status.update(newStatus);
        status = result.getNewStatus();
        return result;
    }

    public BigDecimal getItemsPrice() {
        return items.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDeliveryPrice() {
        if (items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return delivery.getPrice();
    }
}
