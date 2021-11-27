package pl.bookstore.ebook.order.app;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.db.OrderJpaRepository;
import pl.bookstore.ebook.order.domain.Order;
import pl.bookstore.ebook.order.domain.OrderItem;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
  private final OrderJpaRepository repository;
  private final BookJpaRepository catalogRepository;

  @Override
  public List<OrderDto> findAll() {
    return repository.findAll()
            .stream()
            .map(this::toOrderDto)
            .toList();
  }

  @Override
  public Optional<OrderDto> findById(Long id) {
    return repository.findById(id).map(this::toOrderDto);
  }

  private OrderDto toOrderDto(Order order) {
    List<OrderItemDto> richItems = toOrderItemsDto(order.getItems());
    return new OrderDto(
            order.getId(),
            order.getStatus(),
            richItems,
            order.getRecipient(),
            order.getCreatedAt()
    );
  }

  private List<OrderItemDto> toOrderItemsDto(List<OrderItem> items) {
    return items.stream()
            .map(item -> {
              Book book = catalogRepository
                      .findById(item.getBookId())
                      .orElseThrow(() -> new IllegalStateException("Unable to find book with ID: " + item.getBookId()));
              return new OrderItemDto(book, item.getQuantity());
            })
            .toList();
  }
}
