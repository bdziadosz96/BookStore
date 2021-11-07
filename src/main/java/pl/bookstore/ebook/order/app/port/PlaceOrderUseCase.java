package pl.bookstore.ebook.order.app.port;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.Recipient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface PlaceOrderUseCase {
  PlaceOrderResponse placeOrder(PlaceOrderCommand command);

  @Value
  @Builder
  public class PlaceOrderCommand {
    @Singular List<OrderItem> items;
    Recipient recipient;
  }

  @Value
  public class PlaceOrderResponse {
    boolean succes;
    Long orderId;
    List<String> errors;

    public static PlaceOrderResponse success(Long orderId) {
      return new PlaceOrderResponse(true,orderId, Collections.emptyList());
    }

    public static PlaceOrderResponse failure(String ... errors) {
      return new PlaceOrderResponse(false,null, Arrays.asList(errors));
    }

  }
}
