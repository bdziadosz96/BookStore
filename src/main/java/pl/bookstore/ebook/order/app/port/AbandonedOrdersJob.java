package pl.bookstore.ebook.order.app.port;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bookstore.ebook.order.db.OrderJpaRepository;
import pl.bookstore.ebook.order.domain.OrderStatus;

@Component
@AllArgsConstructor
class AbandonedOrdersJob {
    private OrderJpaRepository repository;
    private ManageOrderUseCase orderUseCase;

    @Scheduled(fixedDelay = 60_000)
    public void run() {
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(1);
        var orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, timestamp);
        System.out.println("Orders with status: " + OrderStatus.NEW + " remaining one minute " + orders);
    }
}
