package pl.bookstore.ebook.order.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bookstore.ebook.order.domain.Order;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
  @Override
  @Query("SELECT o FROM Order o JOIN FETCH o.items")
  List<Order> findAll();
}
