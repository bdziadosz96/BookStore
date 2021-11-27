package pl.bookstore.ebook.order.app;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.db.OrderJpaRepository;
import pl.bookstore.ebook.order.domain.Order;
import pl.bookstore.ebook.order.domain.OrderStatus;

@Service
@AllArgsConstructor
class ManageOrderService implements ManageOrderUseCase {
  private final OrderJpaRepository repository;

  @Override
  public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
    final Order order =
        Order.builder().items(command.getItems()).recipient(command.getRecipient()).build();
    Order save = repository.save(order);
    return PlaceOrderResponse.success(save.getId());
  }

  @Override
  public void deleteOrderById(Long id) {
    repository.deleteById(id);
  }

  @Override
  public void updateOrderStatus(Long id, OrderStatus status) {
    repository
        .findById(id)
        .ifPresent(
            order -> {
              order.setStatus(status);
              repository.save(order);
            });
  }
}
