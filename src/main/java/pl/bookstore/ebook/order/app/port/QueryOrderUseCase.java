package pl.bookstore.ebook.order.app.port;

import java.util.List;
import java.util.Optional;

import pl.bookstore.ebook.order.domain.OrderDto;

public interface QueryOrderUseCase {
    List<OrderDto> findAll();

    Optional<OrderDto> findById(Long id);

}
