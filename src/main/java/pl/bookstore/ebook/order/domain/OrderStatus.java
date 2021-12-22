package pl.bookstore.ebook.order.domain;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum OrderStatus {
    NEW {
        @Override
        public UpdateStatusResult update(OrderStatus orderStatus) {
            return switch (orderStatus) {
                case PAID -> UpdateStatusResult.ok(OrderStatus.PAID);
                case CANCELED, ABANDONED -> UpdateStatusResult.failure(OrderStatus.CANCELED);
                default -> super.update(orderStatus);
            };
        }
    },
    PAID {
        @Override
        public UpdateStatusResult update(OrderStatus orderStatus) {
            if (orderStatus == OrderStatus.SHIPPED) {
                return UpdateStatusResult.ok(OrderStatus.SHIPPED);
            }
            return super.update(orderStatus);
        }
    },
    CANCELED,
    ABANDONED,
    SHIPPED;

    public static Optional<OrderStatus> checkString(String value) {
        return Arrays.stream(OrderStatus.values())
                .filter(it -> StringUtils.equalsIgnoreCase(it.name(), value))
                .findFirst();
    }
        //test
    public UpdateStatusResult update(OrderStatus orderStatus) {
        throw new IllegalStateException("Status cannot be update from: " + name()
                + " to: " + orderStatus);
    }
}
