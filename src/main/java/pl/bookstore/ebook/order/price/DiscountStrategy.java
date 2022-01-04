package pl.bookstore.ebook.order.price;

import java.math.BigDecimal;
import pl.bookstore.ebook.order.domain.Order;

interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
