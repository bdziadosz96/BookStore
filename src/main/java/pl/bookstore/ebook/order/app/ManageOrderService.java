package pl.bookstore.ebook.order.app;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.db.OrderJpaRepository;
import pl.bookstore.ebook.order.domain.Order;

@Service
@AllArgsConstructor
class ManageOrderService implements ManageOrderUseCase {
  private final OrderJpaRepository repository;

  @Override
  public PlaceOrderResponse placeOrder(final PlaceOrderCommand command) {
    final Order order =
        Order.builder().items(command.getItems()).recipient(command.getRecipient()).build();
    final Order save = repository.save(order);
    return PlaceOrderResponse.success(save.getId());
  }

  @Override
  public void deleteOrderById(final Long id) {
    repository.deleteById(id);
  }

  @Override
  public void updateOrderStatus(final String status) {
    final Optional<Order> orderOptional = repository.findById(command.getId());
    if (orderOptional.isPresent()) {
      final Order order = orderOptional.get();
      order.updateStatus(command.getStatus());
      repository.save(order);
    }
  }
}
