package pl.bookstore.ebook.order.app;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.domain.Order;
import pl.bookstore.ebook.order.domain.OrderRepository;

@Service
@AllArgsConstructor
class ManageOrderService implements ManageOrderUseCase {
  private final OrderRepository repository;

  @Override
  public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
    final Order order =
        Order.builder()
                .items(command.getItems())
                .recipient(command.getRecipient())
                .build();
    Order save = repository.save(order);
    return PlaceOrderResponse.success(save.getId());
  }

  @Override
  public UpdateOrderStatusResponse updateOrderStatus(UpdateOrderStatusCommand command) {
    command.
  }
}
