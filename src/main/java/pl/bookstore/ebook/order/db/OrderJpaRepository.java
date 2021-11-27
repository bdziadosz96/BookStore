package pl.bookstore.ebook.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bookstore.ebook.order.domain.Order;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order,Long> {
}
