package pl.bookstore.ebook.order.domain;

import lombok.Generated;
import lombok.Value;

@Value
public class UpdateStatusResult {
    OrderStatus newStatus;
    boolean revoked;

    static UpdateStatusResult ok(OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, false);
    }

    static UpdateStatusResult failure(OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, true);
    }
}
