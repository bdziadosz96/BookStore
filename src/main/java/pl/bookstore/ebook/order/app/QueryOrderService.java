package pl.bookstore.ebook.order.app;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.Order;
import pl.bookstore.ebook.order.domain.OrderRepository;

import java.util.List;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
  private final OrderRepository repository;

  @Override
  public List<Order> findAll() {
    return repository.findAll();
  }
}
