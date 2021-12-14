package pl.bookstore.ebook.order.app.port;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import pl.bookstore.ebook.commons.Either;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

public interface ManageOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void deleteOrderById(Long id);

    void updateOrderStatus(Long id, OrderStatus status);

    @Value
    @Builder
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        List<OrderItemCommand> items;
        Recipient recipient;
    }

    @Value
    class UpdateOrderStatusCommand {
        Long id;
        OrderStatus status;
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
}
