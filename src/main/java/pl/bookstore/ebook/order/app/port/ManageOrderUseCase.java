package pl.bookstore.ebook.order.app.port;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ManageOrderUseCase {
  PlaceOrderResponse placeOrder(PlaceOrderCommand command);

  UpdateOrderStatusResponse updateOrderStatus(UpdateOrderStatusCommand command);

  @Value
  @Builder
  class PlaceOrderCommand {
    @Singular List<OrderItem> items;
    Recipient recipient;
  }

  @Value
  class UpdateOrderStatusCommand {
    Long id;
    String status;
  }

  @Value
  class PlaceOrderResponse {
    boolean success;
    Long orderId;
    List<String> errors;

    public static PlaceOrderResponse success(Long orderId) {
      return new PlaceOrderResponse(true, orderId, Collections.emptyList());
    }

    public static PlaceOrderResponse failure(String... errors) {
      return new PlaceOrderResponse(false, null, Arrays.asList(errors));
    }
  }

  @Value
  class UpdateOrderStatusResponse {
    boolean success;
    List<String> errors;

    public static UpdateOrderStatusResponse success() {
      return new UpdateOrderStatusResponse(true,Collections.emptyList());
    }

    public static UpdateOrderStatusResponse failure() {
      return new UpdateOrderStatusResponse(false,Collections.emptyList());
    }
  }
}
