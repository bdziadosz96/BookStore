package pl.bookstore.ebook.order.app;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.db.OrderJpaRepository;
import pl.bookstore.ebook.order.domain.Order;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
    private final OrderJpaRepository repository;

    @Override
    @Transactional
    public List<OrderDto> findAll() {
        return repository.findAll().stream().map(this::toOrderDto).toList();
    }

    @Override
    public Optional<OrderDto> findById(final Long id) {
        return repository.findById(id).map(this::toOrderDto);
    }

    private OrderDto toOrderDto(final Order order) {
        return new OrderDto(
                order.getId(),
                order.getStatus(),
                order.getItems(),
                order.getRecipient(),
                order.getCreatedAt());
    }

}
