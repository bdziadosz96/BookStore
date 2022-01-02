package pl.bookstore.ebook.order.app.port;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.UpdateOrderStatusCommand;
import pl.bookstore.ebook.order.db.OrderJpaRepository;
import pl.bookstore.ebook.order.domain.OrderProperties;
import pl.bookstore.ebook.order.domain.OrderStatus;

@Slf4j
@Component
@AllArgsConstructor
class AbandonedOrdersJob {
    private OrderJpaRepository repository;
    private ManageOrderUseCase orderUseCase;
    private OrderProperties properties;

    @Scheduled(cron = "${app.time.cron-delay-job}")
    public void run() {
        var announcementsLifetime = properties.getAnnouncementsLifetime();
        LocalDateTime timestamp = LocalDateTime.now().minus(announcementsLifetime);
        var orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, timestamp);
        AbandonedOrdersJob.log.info("Orders with status: " + OrderStatus.NEW + " remaining one minute " + orders.size());
        orders.forEach(order -> {
            String admin = "admin@admin.pl";
            UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(order.getId(), OrderStatus.ABANDONED, admin);
            orderUseCase.updateOrderStatus(command);
        });
    }
}
