package pl.bookstore.ebook.order.app.port;

import pl.bookstore.ebook.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {
    List<Order> findAll();
}
