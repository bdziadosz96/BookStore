package pl.bookstore.ebook.order.app;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.catalog.domain.CatalogRepository;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.Order;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
  private final OrderRepository repository;
  private final CatalogRepository catalogRepository;

  @Override
  public List<OrderDto> findAll() {
    return repository.findAll()
            .stream()
            .map(this::toRichOrder)
            .toList();
  }

  @Override
  public Optional<OrderDto> findById(Long id) {
    return repository.findById(id).map(this::toRichOrder);
  }

  private OrderDto toRichOrder(Order order) {
    List<RichOrderItem> richItems = toRichItems(order.getItems());
    return new OrderDto(
            order.getId(),
            order.getStatus(),
            richItems,
            order.getRecipient(),
            order.getCreatedAt()
    );
  }

  private List<RichOrderItem> toRichItems(List<OrderItem> items) {
    return items.stream()
            .map(item -> {
              Book book = catalogRepository
                      .findById(item.getBookId())
                      .orElseThrow(() -> new IllegalStateException("Unable to find book with ID: " + item.getBookId()));
              return new RichOrderItem(book, item.getQuantity());
            })
            .toList();
  }
}
