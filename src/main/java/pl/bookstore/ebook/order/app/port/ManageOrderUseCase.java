package pl.bookstore.ebook.order.app.port;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import pl.bookstore.ebook.commons.Either;
import pl.bookstore.ebook.order.domain.Delivery;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

public interface ManageOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void deleteOrderById(Long id);

    UpdateStatusResponse updateOrderStatus(UpdateOrderStatusCommand command);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular List<OrderItemCommand> items;
        Recipient recipient;
        @Builder.Default Delivery delivery = Delivery.COURIER;
    }

    @Value
    @Builder
    @AllArgsConstructor
    class UpdateOrderStatusCommand {
        Long orderId;
        OrderStatus status;
        UserDetails user;
    }

    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    class PlaceOrderResponse extends Either<String, Long> {
        PlaceOrderResponse(final boolean success, final String left, final Long right) {
            super(success, left, right);
        }

        public static PlaceOrderResponse success(final Long orderId) {
            return new PlaceOrderResponse(true, null, orderId);
        }

        public static PlaceOrderResponse failure(final String errors) {
            return new PlaceOrderResponse(false, errors, null);
        }
    }

    class UpdateStatusResponse extends Either<Error, OrderStatus> {
        UpdateStatusResponse(final boolean success, final Error left, final OrderStatus right) {
            super(success, left, right);
        }

        public static UpdateStatusResponse success(OrderStatus status) {
            return new UpdateStatusResponse(true, null, status);
        }

        public static UpdateStatusResponse failure(Error error) {
            return new UpdateStatusResponse(false, error, null);
        }
    }

    @AllArgsConstructor
    @Getter
    enum Error {
        NOT_FOUND(HttpStatus.NOT_FOUND),
        FORBIDDEN(HttpStatus.FORBIDDEN);

        private final HttpStatus status;
    }
}
