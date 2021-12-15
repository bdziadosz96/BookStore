package pl.bookstore.ebook.order.domain;

record UpdateStatusResult(OrderStatus newStatus, boolean revoked) {
    static UpdateStatusResult ok(final OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, false);
    }

    static UpdateStatusResult failure(final OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, true);
    }
}
