package pl.bookstore.ebook.order.price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import pl.bookstore.ebook.order.domain.Order;

class TotalPriceDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        BigDecimal lowestBookPrice;
        if (isGreaterOrEqualTo(order, 400)) {
            return cheapestBookPrice(order);
        } else if (isGreaterOrEqualTo(order, 200)) {
            lowestBookPrice = cheapestBookPrice(order);
            return lowestBookPrice.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal cheapestBookPrice(Order order) {
        return order.getItems().stream()
                .map(item -> item.getBook().getPrice())
                .sorted()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private boolean isGreaterOrEqualTo(Order order, int price) {
        return order.getItemsPrice().compareTo(BigDecimal.valueOf(price)) >= 0;
    }
}
