package pl.bookstore.ebook.order.price;

import java.math.BigDecimal;
import pl.bookstore.ebook.order.domain.Order;

class DeliveryDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        if (order.getItemsPrice().compareTo(new BigDecimal("100")) >= 0) {
            return order.getDeliveryPrice();
        }
        return BigDecimal.ZERO;
    }
}
