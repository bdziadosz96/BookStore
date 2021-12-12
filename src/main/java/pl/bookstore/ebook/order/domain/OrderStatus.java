package pl.bookstore.ebook.order.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatus {
    NEW {
        @Override
        public OrderStatus updateStatus(OrderStatus orderStatus) {
            return switch (orderStatus) {
                case NEW -> NEW;
                case CANCELED -> CANCELED;
                case ABANDONED -> ABANDONED;
                default -> super.updateStatus(orderStatus);
            };
        }
    },
    PAID {
      @Override
      public OrderStatus updateStatus(OrderStatus orderStatus) {
        return switch (orderStatus) {
          case SHIPPED -> SHIPPED;
          default -> super.updateStatus(orderStatus);
        };
      }
    },
    CANCELED,
    ABANDONED,
    SHIPPED;

    public static Optional<OrderStatus> checkString(String value) {
        return Arrays.stream(values())
                .filter(it -> StringUtils.equalsIgnoreCase(it.name(), value))
                .findFirst();
    }

    public OrderStatus updateStatus(OrderStatus orderStatus) {
        throw new IllegalStateException("Status cannot be update from: " + this.name()
                + " to following value" + orderStatus);
    }
}
