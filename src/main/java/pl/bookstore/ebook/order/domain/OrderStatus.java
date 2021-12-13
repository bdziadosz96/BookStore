package pl.bookstore.ebook.order.domain;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum OrderStatus {
    NEW {
        @Override
        public OrderStatus update(final OrderStatus orderStatus) {
            return switch (orderStatus) {
                case PAID -> PAID;
                case CANCELED -> CANCELED;
                case ABANDONED -> ABANDONED;
                default -> super.update(orderStatus);
            };
        }
    },
    PAID {
      @Override
      public OrderStatus update(final OrderStatus orderStatus) {
          if (orderStatus == SHIPPED) {
              return SHIPPED;
          }
          return super.update(orderStatus);
      }
    },
    CANCELED,
    ABANDONED,
    SHIPPED;

    public static boolean checkString(final String value) {
        final Optional<OrderStatus> first = Arrays.stream(values())
                .filter(it -> StringUtils.equalsIgnoreCase(it.name(), value))
                .findFirst();
        return first.isPresent();
    }

    public OrderStatus update(final OrderStatus orderStatus) {
        throw new IllegalStateException("Status cannot be update from: " + name()
                + " to: " + orderStatus);
    }
}
