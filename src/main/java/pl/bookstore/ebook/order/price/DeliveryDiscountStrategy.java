package pl.bookstore.ebook.order.price;

import java.math.BigDecimal;
import pl.bookstore.ebook.order.domain.Order;

class DeliveryDiscountStrategy implements DiscountStrategy {
    public static final BigDecimal THRESHOLD = BigDecimal.valueOf(100);

    @Override
    public BigDecimal calculate(Order order) {
        if(order.getItemsPrice().compareTo(THRESHOLD) >= 0) {
            return order.getDeliveryPrice();
        }
        return BigDecimal.ZERO;
    }
}
