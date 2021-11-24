package pl.bookstore.ebook.order.app.port;

import pl.bookstore.ebook.order.domain.Order;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {
    List<Order> findAll();

    Optional<Order> findById(Long id);
}
